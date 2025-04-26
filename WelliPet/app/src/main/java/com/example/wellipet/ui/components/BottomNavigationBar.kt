package com.example.wellipet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.wellipet.R
import com.example.wellipet.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // 跟 TopBar 同樣的 Press Start 2P 字體設定
    val pressStart = GoogleFont("Press Start 2P")
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val cuteFont = FontFamily(
        Font(googleFont = pressStart, fontProvider = provider, weight = FontWeight.Bold)
    )

    // 漸層＋圓角＋陰影
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFF8E0CB), Color(0xFFFACE76))
                )
            )
            .shadow(1.5.dp, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        color = Color.Transparent,
        contentColor = Color.White
    ) {
        val items = listOf(Screen.Home, Screen.HealthData, Screen.Store)
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            val current = navController.currentBackStackEntryAsState().value?.destination
            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        when (screen) {
                            Screen.Home -> Icon(Icons.Default.Home, contentDescription = null)
                            Screen.HealthData -> Icon(Icons.Default.Favorite, contentDescription = null)
                            Screen.Store -> Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            else -> Icon(Icons.Default.Home, contentDescription = null)
                        }
                    },
                    label = {
                        Text(
                            screen.label,
                            fontFamily = cuteFont,
                            fontSize = 10.sp,
                            color = if (current?.route == screen.route) Color(0xFF4F2603) else Color.DarkGray
                        )
                    },
                    selected = current?.route == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    alwaysShowLabel = true,
                    interactionSource = remember { MutableInteractionSource() },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0x167A4E03),
                        selectedIconColor = Color(0xFF492501),
                        unselectedIconColor = Color.DarkGray
                    )
                )
            }
        }
    }
}