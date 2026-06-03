package com.montanhajr.calculejuros.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.montanhajr.calculejuros.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val SoraFont = FontFamily(
    Font(googleFont = GoogleFont("Sora"), fontProvider = provider)
)

val DmSansFont = FontFamily(
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider)
)

val JetBrainsMonoFont = FontFamily(
    Font(googleFont = GoogleFont("JetBrains Mono"), fontProvider = provider)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = SoraFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SoraFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SoraFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = DmSansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DmSansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = DmSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)

val MonoValueStyle = TextStyle(
    fontFamily = JetBrainsMonoFont,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)
