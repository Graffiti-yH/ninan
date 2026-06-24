package com.denser.june.presentation.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.denser.june.core.R
import com.denser.june.core.domain.model.ai.AiAnalysisResult
import com.denser.june.presentation.components.JuneAppBarType
import com.denser.june.presentation.components.JuneTopAppBar
import com.denser.june.presentation.navigation.AppNavigator
import com.denser.june.presentation.navigation.Route
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiScreen() {
    val vm: AiVM = koinViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val navigator = koinInject<AppNavigator>()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showStandardDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            JuneTopAppBar(
                type = JuneAppBarType.Medium,
                title = { Text(stringResource(R.string.ai_analysis_title)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showStandardDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.info_24px),
                            contentDescription = stringResource(R.string.ai_analysis_standard)
                        )
                    }
                    IconButton(onClick = { navigator.navigateTo(Route.AiSettings) }) {
                        Icon(
                            painter = painterResource(R.drawable.track_changes_24px),
                            contentDescription = stringResource(R.string.ai_settings_title)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date range selector
            DateRangeSelector(
                selectedRange = state.selectedRange,
                onSelectRange = vm::onSelectRange,
                enabled = state.analysisState !is AnalysisState.Loading
            )

            // Analyze button
            Button(
                onClick = { vm.analyze() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = state.analysisState !is AnalysisState.Loading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.explore_24px),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        when (state.analysisState) {
                            is AnalysisState.Loading -> R.string.ai_analyzing
                            is AnalysisState.Success -> R.string.ai_re_analyze
                            else -> R.string.ai_start_analysis
                        }
                    )
                )
            }

            // Journal count info during loading
            if (state.journalCount > 0 && state.analysisState is AnalysisState.Loading) {
                Text(
                    text = stringResource(R.string.ai_analyzing_entries, state.journalCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Loading indicator
            if (state.analysisState is AnalysisState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            // Error state
            if (state.analysisState is AnalysisState.Error) {
                ErrorCard(
                    message = (state.analysisState as AnalysisState.Error).message,
                    onRetry = { vm.analyze() },
                    onReset = { vm.reset() }
                )
            }

            // Success state — results
            if (state.analysisState is AnalysisState.Success) {
                val result = (state.analysisState as AnalysisState.Success).result
                AnalysisResults(result = result, journalCount = state.journalCount)
            }

            // Idle state hint
            if (state.analysisState is AnalysisState.Idle) {
                IdleHint()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showStandardDialog) {
        AiStandardDialog(onDismiss = { showStandardDialog = false })
    }
}

@Composable
private fun AiStandardDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.info_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.ai_analysis_standard))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.ai_standard_intro),
                    style = MaterialTheme.typography.bodyMedium
                )

                // CBT Framework
                Text(
                    text = stringResource(R.string.ai_standard_framework_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.ai_standard_framework_desc),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Analysis Dimensions
                Text(
                    text = stringResource(R.string.ai_standard_dimensions_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.ai_standard_dimensions_desc),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Cognitive Distortions
                Text(
                    text = stringResource(R.string.ai_standard_distortions_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = stringResource(R.string.ai_standard_distortions_desc),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Mood Trend
                Text(
                    text = stringResource(R.string.ai_standard_trend_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.ai_standard_trend_desc),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Disclaimer
                Divider()
                Text(
                    text = stringResource(R.string.ai_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.got_it))
            }
        }
    )
}

@Composable
private fun DateRangeSelector(
    selectedRange: AiDateRange,
    onSelectRange: (AiDateRange) -> Unit,
    enabled: Boolean
) {
    Text(
        text = stringResource(R.string.ai_date_range_label),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AiDateRange.entries.forEach { range ->
            FilterChip(
                selected = selectedRange == range,
                onClick = { onSelectRange(range) },
                label = { Text(stringResource(range.labelRes)) },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun IdleHint() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.explore_24px),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(R.string.ai_idle_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.warning_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ai_analysis_failed),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onReset) {
                    Text(stringResource(R.string.reset))
                }
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.ai_re_analyze))
                }
            }
        }
    }
}

@Composable
private fun AnalysisResults(
    result: AiAnalysisResult,
    journalCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Header: entries analyzed
        Text(
            text = stringResource(R.string.ai_entries_analyzed, journalCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Overall Mood Card
        OverallMoodCard(result)

        // Emotion Summary
        if (result.emotionSummary.isNotBlank()) {
            SectionCard(
                title = stringResource(R.string.ai_emotion_summary),
                icon = R.drawable.sentiment_very_satisfied_24px_fill
            ) {
                Text(
                    text = result.emotionSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Key Themes
        if (result.keyThemes.isNotEmpty()) {
            SectionCard(
                title = stringResource(R.string.ai_key_themes),
                icon = R.drawable.bookmark_24px_fill
            ) {
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    result.keyThemes.forEach { theme ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(theme) }
                        )
                    }
                }
            }
        }

        // Emotional Patterns
        if (result.emotionalPatterns.isNotEmpty()) {
            SectionCard(
                title = stringResource(R.string.ai_emotional_patterns),
                icon = R.drawable.repeat_24px
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.emotionalPatterns.forEach { pattern ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = pattern,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Cognitive Distortions
        if (result.cognitiveDistortions.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.warning_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.ai_cognitive_distortions),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        result.cognitiveDistortions.forEach { distortion ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = distortion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Suggestions
        if (result.suggestions.isNotEmpty()) {
            SectionCard(
                title = stringResource(R.string.ai_suggestions),
                icon = R.drawable.light_mode_24px
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    result.suggestions.forEachIndexed { index, suggestion ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Positive Highlights
        if (result.positiveHighlights.isNotEmpty()) {
            SectionCard(
                title = stringResource(R.string.ai_positive_highlights),
                icon = R.drawable.check_circle_24px_fill
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.positiveHighlights.forEach { highlight ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "✨",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = highlight,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Risk Warning
        val riskWarning = result.riskWarning
        if (!riskWarning.isNullOrBlank()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.warning_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.ai_risk_warning),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = riskWarning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Disclaimer
        Text(
            text = stringResource(R.string.ai_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun OverallMoodCard(result: AiAnalysisResult) {
    val (trendIcon, trendColor) = when (result.moodTrend) {
        "improving" -> R.drawable.arrow_upward_24px to MaterialTheme.colorScheme.primary
        "declining" -> R.drawable.arrow_upward_24px to MaterialTheme.colorScheme.error
        else -> R.drawable.arrow_forward_24px to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.ai_overall_mood),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.overallMood,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Trend indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(trendIcon),
                    contentDescription = result.moodTrend,
                    tint = trendColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (result.moodTrend) {
                        "improving" -> stringResource(R.string.ai_trend_improving)
                        "declining" -> stringResource(R.string.ai_trend_declining)
                        else -> stringResource(R.string.ai_trend_stable)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = trendColor
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}
