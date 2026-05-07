package com.denser.june.core.domain.model

import com.denser.june.core.domain.model.enums.ThemeMode
import com.materialkolor.PaletteStyle

data class AppTheme(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val seedColor: Int = -1,
    val withAmoled: Boolean = false,
    val style: PaletteStyle = PaletteStyle.TonalSpot,
    val materialTheme: Boolean = false,
    val appFont: String = "Google Sans Flex",
)
