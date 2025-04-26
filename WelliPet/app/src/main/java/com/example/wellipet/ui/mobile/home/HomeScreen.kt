// File: com/example/wellipet/ui/mobile/home/HomeScreen.kt
package com.example.wellipet.ui.mobile.home

import RequestLocationPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import com.example.wellipet.R
import com.example.wellipet.ui.components.BottomNavigationBar
import com.example.wellipet.ui.mobile.store.StoreViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuteTopBar() {
    // ---- Step.1 定義字體 ----
    val Cus = GoogleFont("Press Start 2P")
    val CusProvider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val CusFont = FontFamily(
        Font(googleFont = Cus, fontProvider = CusProvider, weight = FontWeight.Bold)
    )

    // ---- Step.2 畫出 AppBar ----
    TopAppBar(
        // 先讓 AppBar 自身透明，並在外層加上漸層背景
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFF8E0CB), Color(0xFFFACE76))
                )
            )
            .shadow(2.dp, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
        title = {
            Text(
                "WelliPet",
                fontFamily = CusFont,
                fontSize = 28.sp,
                color = Color(0xFF4F2603)
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent  // 透明底色
//            titleContentColor = Color(0xFF020202)
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(),
    storeViewModel: StoreViewModel = viewModel()   // 請確保此 viewModel 屬於共享範圍
) {
    RequestLocationPermission { granted ->
        // HomeViewModel 已經在 init 中處理位置更新與天氣刷新
    }

    val weatherResponse by homeViewModel.weatherResponse.collectAsState()

    // 從 StoreViewModel 中讀取永久保存的選擇（DataStore）
    val selectedPet by storeViewModel.selectedPet.collectAsState()
    val selectedBackground by storeViewModel.selectedBackground.collectAsState()
    val petRes = selectedPet ?: R.drawable.dog      // 預設寵物圖片
    val backgroundRes = selectedBackground ?: R.drawable.bg_park  // 預設背景圖片

    val context = LocalContext.current
    val gifImageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(ImageDecoderDecoder.Factory()) }
            .build()
    }

    Scaffold(
        topBar = { CuteTopBar() },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 背景圖：根據 StoreViewModel 選擇更新
            AsyncImage(
                model = backgroundRes,
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // 天氣資訊區塊（固定位置）
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                if (weatherResponse != null) {
                    Text(
                        text = "City: ${weatherResponse!!.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Text(
                        text = "Temp: ${weatherResponse!!.main.temp}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    weatherResponse!!.weather.forEach { weather ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            val iconRes = com.example.wellipet.utils.getWeatherIconRes(weather.description)
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = weather.description,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = weather.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                        val suggestion = com.example.wellipet.utils.getSuggestionText(weather.description)
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "Loading weather...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
            // 中央顯示寵物內容
            Column(
                modifier = Modifier.fillMaxSize()
                .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = petRes,
                    imageLoader = gifImageLoader,
                    contentDescription = "Selected Pet",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Feeling Happy",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Daily Health progress: Steps / Sleep / Water ...",
                    color = Color.White
                )
            }
        }
    }
}
