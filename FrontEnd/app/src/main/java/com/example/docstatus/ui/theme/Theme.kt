package com.example.docstatus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Темная цветовая схема приложения, основанная на кастомной палитре [AppColors].
 */
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryBlue,
    background = AppColors.Background,
    surface = AppColors.Surface,
)

/**
 * Главная тема приложения DocStatus.
 *
 * Эта Composable-функция применяет цветовую схему и типографику приложения
 * к переданному в нее контенту.
 *
 * @param content Composable-контент, к которому будет применена тема.
 */
@Composable
fun DocStatusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
