package com.denser.june.presentation.screens.settings.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.denser.june.core.domain.backup.ExportState
import com.denser.june.core.domain.backup.RestoreState
import com.denser.june.presentation.navigation.AppNavigator
import com.denser.june.presentation.components.JuneAppBarType
import com.denser.june.presentation.components.JuneTopAppBar
import com.denser.june.presentation.screens.settings.SettingsAction
import org.koin.compose.koinInject

import com.denser.june.core.R
import com.denser.june.presentation.components.JuneDialog
import com.denser.june.core.domain.backup.RestoreFailedException
import com.denser.june.presentation.screens.settings.SettingsVM
import com.denser.june.presentation.screens.settings.components.SettingSection
import com.denser.june.presentation.screens.settings.components.SettingsItem
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BackupScreen() {
    val settingsVM: SettingsVM = koinViewModel()
    val state = settingsVM.state.collectAsStateWithLifecycle().value
    val onAction = settingsVM::onAction
    val context = LocalContext.current
    val navigator = koinInject<AppNavigator>()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var showExportDialog by remember { mutableStateOf(false) }
    var showRestoreWarning by remember { mutableStateOf<String?>(null) }

    var includeMedia by remember { mutableStateOf(true) }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { targetUri ->
            if (state.exportState is ExportState.ExportReady) {
                try {
                    val tempFile = state.exportState.file
                    context.contentResolver.openOutputStream(targetUri)?.use { output ->
                        tempFile.inputStream().use { input -> input.copyTo(output) }
                    }
                    Toast.makeText(context, context.getString(R.string.backup_saved), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.backup_failed_save), Toast.LENGTH_SHORT).show()
                } finally {
                    onAction(SettingsAction.ResetBackup)
                }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            showRestoreWarning = it.toString()
        }
    }

    LaunchedEffect(state.exportState) {
        if (state.exportState is ExportState.ExportReady) {
            val fileName = "June_Backup_${System.currentTimeMillis()}.zip"
            saveLauncher.launch(fileName)
        }
    }

    LaunchedEffect(state.restoreState) {
        when (state.restoreState) {
            is RestoreState.Restored -> {
                Toast.makeText(context, context.getString(R.string.restore_complete), Toast.LENGTH_SHORT).show()
                onAction(SettingsAction.ResetBackup)
            }

            is RestoreState.Failure -> {
                val errorMsg = when (state.restoreState.exception) {
                    RestoreFailedException.InvalidFile -> context.getString(R.string.invalid_backup_file)
                    RestoreFailedException.OldSchema -> context.getString(R.string.backup_format_old)
                }
                Toast.makeText(context, context.getString(R.string.restore_failed, errorMsg), Toast.LENGTH_LONG).show()
                onAction(SettingsAction.ResetBackup)
            }

            else -> Unit
        }
    }

    BackHandler { navigator.navigateBack() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            JuneTopAppBar(
                type = JuneAppBarType.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(stringResource(R.string.local_backup)) },
                navigationIcon = {
                    FilledIconButton(
                        onClick = { navigator.navigateBack() },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        ),
                    ) {
                        Icon(painterResource(R.drawable.arrow_back_24px), "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.backup_page_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                SettingSection {
                    SettingsItem(
                        title = stringResource(R.string.export_data),
                        subtitle = "Save your journals and media to a secure file.",
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.upload_24px),
                                null
                            )
                        }
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (state.exportState !is ExportState.Exporting) {
                                    showExportDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.exportState !is ExportState.Exporting
                        ) {
                            if (state.exportState is ExportState.Exporting) {
                                CircularWavyProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creating Backup...")
                            } else {
                                Icon(
                                    painterResource(R.drawable.save_24px),
                                    null,
                                    Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.create_backup))
                            }
                        }
                    }

                    SettingsItem(
                        title = stringResource(R.string.restore_data),
                        subtitle = "Import data from a previously saved backup file.",
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.download_24px),
                                null
                            )
                        }
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                restoreLauncher.launch(
                                    arrayOf(
                                        "application/zip",
                                        "application/json",
                                        "*/*"
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.restoreState !is RestoreState.Restoring
                        ) {
                            if (state.restoreState is RestoreState.Restoring) {
                                CircularWavyProgressIndicator(
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Restoring...")
                            } else {
                                Icon(
                                    painterResource(R.drawable.folder_open_24px),
                                    null,
                                    Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.select_backup_file))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp + innerPadding.calculateBottomPadding()))
        }
    }

    if (showExportDialog) {
        JuneDialog(
            onDismissRequest = { showExportDialog = false },
            title = stringResource(R.string.create_backup_title),
            icon = R.drawable.upload_24px,
            confirmButton = {
                Button(
                    onClick = {
                        showExportDialog = false
                        onAction(SettingsAction.OnExportJournals(includeMedia))
                    }
                ) { Text(stringResource(R.string.create)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExportDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
            text = {
                Column {
                    Text("This will create a ZIP file containing your journal entries.")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.include_media),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = includeMedia,
                            onCheckedChange = { includeMedia = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (includeMedia) "Backup size might be large. Photos & videos will be included." else "Photos & videos will not be saved. Faster and smaller.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    if (showRestoreWarning != null) {
        JuneDialog(
            onDismissRequest = { showRestoreWarning = null },
            title = stringResource(R.string.restore_backup_title),
            icon = R.drawable.cloud_sync_24px,
            confirmButton = {
                Button(
                    onClick = {
                        val uri = showRestoreWarning!!
                        showRestoreWarning = null
                        onAction(SettingsAction.OnRestoreJournals(uri))
                    }
                ) { Text(stringResource(R.string.restore)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRestoreWarning = null }) { Text(stringResource(R.string.cancel)) }
            },
            text = {
                Text(stringResource(R.string.restore_warning))
            }
        )
    }
}