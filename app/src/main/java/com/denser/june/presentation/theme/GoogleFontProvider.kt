package com.denser.june.presentation.theme

import androidx.compose.ui.text.googlefonts.GoogleFont
import com.denser.june.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
