// File: com/example/wellipet/ui/mobile/home/HomeScreen.kt
package com.example.wellipet.ui.mobile.home

import RequestLocationPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.wellipet.utils.getWeatherIconRes
import com.example.wellipet.utils.getSuggestionText
import com.example.wellipet.api.WeatherInfo
import com.example.wellipet.ui.components.CuteTopBar

@Composable
fun WeatherCard(
    modifier: Modifier = Modifier,
    city: String,
    temp: Double,
    weatherList: List<WeatherInfo>
) {
    // 半透明黑底、圓角、陰影
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()   // 改成填滿寬度
        ) {
            // 標題列：城市 ＋ 溫度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = city,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "${"%.0f".format(temp)}°C",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(6.dp))

            // 天氣項目列表
            weatherList.forEach { weather ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()         // 讓這一行也填滿寬度
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = getWeatherIconRes(weather.description)),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = weather.description
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = getSuggestionText(weather.description),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}



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
        topBar = { CuteTopBar(
            title     = "WelliPet",
            fontSize  = 32.sp,
            titleColor= Color(0xFF6B3E1E),
            gradient  = Brush.horizontalGradient(
                listOf(Color(0xFFF8E0CB), Color(0xFFFACE76))
            ),
            elevation = 4f
        ) },
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
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                if (weatherResponse != null) {
                    WeatherCard(
                        city = weatherResponse!!.name,
                        temp = weatherResponse!!.main.temp,
                        weatherList = weatherResponse!!.weather,
                        modifier = Modifier.fillMaxWidth(0.6f) // 卡片寬度調整
                    )
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Loading weather...",
                            modifier = Modifier
                                .padding(12.dp),
                            color = Color.White
                        )
                    }
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
