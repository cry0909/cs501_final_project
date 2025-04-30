// File: com/example/wellipet/ui/components/CuteTopBar.kt
package com.example.wellipet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import androidx.compose.ui.text.googlefonts.Font as GoogleFontFont
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellipet.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuteTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    fontSize: TextUnit = 28.sp,
    titleColor: Color = Color(0xFF4F2603),
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFF8E0CB), Color(0xFFFACE76))
    ),
    elevation: Float = 2f,
    actions: @Composable RowScope.() -> Unit = {}      // ← 新增 actions slot
) {
    // 1. 準備 GoogleFont
    val pressStart = GoogleFont("Press Start 2P")
    val provider = Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage   = "com.google.android.gms",
        certificates      = R.array.com_google_android_gms_fonts_certs
    )
    val fontFamily = FontFamily(
        GoogleFontFont(pressStart, provider, weight = FontWeight.Bold)
    )

    // 2. 畫 TopAppBar
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .shadow(elevation.dp, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        title = {
            Text(
                text = title,
                fontFamily = fontFamily,
                fontSize   = fontSize,
                color      = titleColor
            )
        },
        actions = actions,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
