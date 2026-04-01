package com.denser.june.core.domain.data_classes

import com.denser.june.core.domain.enums.ThemeMode
import com.denser.june.core.domain.enums.Fonts
import com.materialkolor.PaletteStyle

data class AppTheme(
    val seedColor: Int = -1,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val withAmoled: Boolean = false,
    val style: PaletteStyle = PaletteStyle.Neutral,
    val materialTheme: Boolean = false,
    val font: Fonts = Fonts.FIGTREE
)