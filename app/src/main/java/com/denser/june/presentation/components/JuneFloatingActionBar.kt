package com.denser.june.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun JuneFloatingActionBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = containerColor,
                    shape = CircleShape
                )
                .padding(6.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun JuneFloatingAction(
    onClick: () -> Unit,
    label: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    disabledContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
) {
    ExtendedFloatingActionButton(
        onClick = { if (enabled) onClick() },
        expanded = true,
        icon = icon,
        text = { Text(label) },
        containerColor = if (enabled) containerColor else disabledContainerColor,
        contentColor = if (enabled) contentColor else disabledContentColor,
        shape = CircleShape,
        modifier = modifier.height(40.dp),
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
    )
}
