package com.denser.june.presentation.screens.settings.screens.ai

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.denser.june.core.R
import com.denser.june.core.domain.preferences.AiPreferences
import com.denser.june.presentation.components.JuneAppBarType
import com.denser.june.presentation.components.JuneTopAppBar
import com.denser.june.presentation.navigation.AppNavigator
import com.denser.june.presentation.screens.settings.components.SettingSection
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.compose.koinInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiSettingsScreen() {
    val navigator = koinInject<AppNavigator>()
    val aiPreferences = koinInject<AiPreferences>()
    val okHttpClient = koinInject<OkHttpClient>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val apiUrl by aiPreferences.getApiUrl().collectAsStateWithLifecycle(initialValue = "")
    val apiKey by aiPreferences.getApiKey().collectAsStateWithLifecycle(initialValue = "")
    val model by aiPreferences.getModel().collectAsStateWithLifecycle(initialValue = "")

    var showKey by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            JuneTopAppBar(
                type = JuneAppBarType.Medium,
                title = { Text(stringResource(R.string.ai_settings_title)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // API Configuration Section
            SettingSection(title = stringResource(R.string.ai_api_config)) {
                // API URL
                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { scope.launch { aiPreferences.setApiUrl(it) } },
                    label = { Text(stringResource(R.string.ai_api_url_label)) },
                    placeholder = { Text("https://api.deepseek.com/v1") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // API Key
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { scope.launch { aiPreferences.setApiKey(it) } },
                    label = { Text(stringResource(R.string.ai_api_key_label)) },
                    placeholder = { Text("sk-...") },
                    singleLine = true,
                    visualTransformation = if (showKey) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                painter = painterResource(
                                    id = if (showKey) R.drawable.visibility_off_24px
                                    else R.drawable.visibility_24px
                                ),
                                contentDescription = if (showKey) {
                                    stringResource(R.string.hide_api_key)
                                } else {
                                    stringResource(R.string.show_api_key)
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Model name
                OutlinedTextField(
                    value = model,
                    onValueChange = { scope.launch { aiPreferences.setModel(it) } },
                    label = { Text(stringResource(R.string.ai_model_label)) },
                    placeholder = { Text("deepseek-chat") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Privacy Notice Section
            SettingSection(title = stringResource(R.string.ai_privacy_title)) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.ai_privacy_notice),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Test Connection Button
            Button(
                onClick = {
                    isTesting = true
                    scope.launch {
                        val result = testConnection(okHttpClient, apiUrl, apiKey)
                        isTesting = false
                        val message = if (result) {
                            context.getString(R.string.connection_successful)
                        } else {
                            context.getString(R.string.connection_failed, "Check your API URL and key")
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = apiUrl.isNotBlank() && apiKey.isNotBlank() && !isTesting
            ) {
                if (isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isTesting) stringResource(R.string.testing) 
                    else stringResource(R.string.ai_test_connection)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private suspend fun testConnection(
    okHttpClient: OkHttpClient,
    apiUrl: String,
    apiKey: String
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${apiUrl.trimEnd('/')}/models")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}
