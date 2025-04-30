// File: com/example/wellipet/ui/mobile/home/HomeScreen.kt
package com.example.wellipet.ui.mobile.home

import RequestLocationPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.wellipet.ui.auth.AuthViewModel
import com.example.wellipet.utils.getWeatherIconRes
import com.example.wellipet.utils.getSuggestionText
import com.example.wellipet.api.WeatherInfo
import com.example.wellipet.ui.components.CuteTopBar
import com.example.wellipet.data.AuthPreferences.setRememberMe
import com.example.wellipet.navigation.Screen
import com.example.wellipet.ui.model.PetGifMapper
import kotlinx.coroutines.launch


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
    storeViewModel: StoreViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),    // 登出用

) {
    RequestLocationPermission { granted ->
        // HomeViewModel 已經在 init 中處理位置更新與天氣刷新
    }

    val weatherResponse by homeViewModel.weatherResponse.collectAsState()

    // 從 StoreViewModel 中讀取永久保存的選擇（DataStore）
    val selectedPet by storeViewModel.selectedPet.collectAsState()
    val selectedBackground by storeViewModel.selectedBackground.collectAsState()
    val selectedBadges by storeViewModel.selectedBadges.collectAsState()
    val petStatus      by homeViewModel.petStatus.collectAsState()

    val backgroundRes = selectedBackground ?: R.drawable.bg_park  // 預設背景圖片
    val gifRes = PetGifMapper.get(
        selectedPet ?: R.drawable.pet_dog,
        petStatus   ?: "happy"
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val gifImageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(ImageDecoderDecoder.Factory()) }
            .build()
    }

    // 將 badge ID 轉回 drawable resource
    val badgeResList = remember(selectedBadges) {
        selectedBadges.mapNotNull { id ->
            val res = context.resources.getIdentifier(id, "drawable", context.packageName)
            res.takeIf { it != 0 }
        }
    }

    var menuExpanded by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val menuBg = Color(0xFFF8E0CB)
    val itemText = Color(0xFF6B3E1E)

    Scaffold(
        topBar = {
            CuteTopBar(
                title     = "WelliPet",
                fontSize  = 32.sp,
                titleColor= Color(0xFF6B3E1E),
                gradient  = Brush.horizontalGradient(
                    listOf(Color(0xFFF8E0CB), Color(0xFFFACE76))
                ),
                elevation = 4f,

                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(menuBg)
                    ) {
                        DropdownMenuItem(
                            text = { Text("About", color = itemText) },
                            onClick = {
                                menuExpanded = false
                                showAboutDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share", color = itemText) },
                            onClick = {
                                menuExpanded = false
                                // TODO: 呼叫分享 Intent
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout", color = itemText) },
                            onClick = {
                                menuExpanded = false
                                scope.launch {
                                    // 1) 清除“记住我”
                                    context.setRememberMe(false)
                                    // 2) Firebase 登出
                                    authViewModel.signOut()
                                    // 3) 导航回 Login，并清空回退栈
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 背景
            AsyncImage(
                model = backgroundRes,
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 天氣卡片
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
                        modifier = Modifier.width(200.dp)
                    )
                }
            }

            // 寵物展示
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = gifRes,
                    imageLoader = gifImageLoader,
                    contentDescription = "Pet",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // 已選徽章列
            if (badgeResList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    badgeResList.forEach { resId ->
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            if (showAboutDialog) {
                val scrollState = rememberScrollState()
                AlertDialog(
                    onDismissRequest = { showAboutDialog = false },
                    containerColor = Color(0xFFF8E0CB),
                    title = { Text("About WelliPet") },
                    text = {
                        // 限制高度到 300.dp，超过就滚动
                        Box(
                            Modifier
                                .heightIn(max = 300.dp)
                                .verticalScroll(scrollState)
                        ) {
                            Text(
                                """
                    WelliPet v1.0

                    WelliPet is a virtual pet companion that grows alongside your healthy habits—every glass of water you drink, every step you take, and every night of restful sleep helps your pet thrive. As you hit your hydration, activity, and sleep goals, you’ll unlock fun badges, and discover new customization options to dress up and decorate your pet’s world (This feature coming soon).

                    According to the World Health Organization’s recommendations, we encourage players to achieve the following daily goals: 5,000 steps, 7 hours of sleep, and 2,000 ml of water intake. Let’s work together for our health!
                    
                    Badges:
                    💧 Hydration Novice (Hydration): Single-day hydration ≥ 2000 ml
                    🚰 Hydration Expert (Hydration): 7 consecutive days with daily hydration ≥ 2000 ml
                    🌊 Hydration Master (Hydration): 14 consecutive days with daily hydration ≥ 2000 ml
                    🔱 Hydration Legend (Hydration): 30 consecutive days with daily hydration ≥ 2000 ml

                    👟 Step Beginner (Steps): Single-day step count ≥ 5,000 steps
                    🏃‍♂️ Jogger (Steps): Single-day step count ≥ 10,000 steps
                    🥇 Step Sprinter (Steps): 7 consecutive days with daily step count ≥ 10,000 steps
                    🏅 Step Champion (Steps): 14 consecutive days with daily step count ≥ 10,000 steps
                    🏆 Step Legend (Steps): 30 consecutive days with daily step count ≥ 10,000 steps

                    🛌 Sleep Enthusiast (Sleep): Single-day sleep duration ≥ 7 hours
                    🌙 Dream Weaver (Sleep): 7 consecutive days with daily sleep ≥ 7 hours
                    ⭐ Sleep Master (Sleep): 14 consecutive days with daily sleep ≥ 7 hours
                    🌌 Sleep Legend (Sleep): 30 consecutive days with daily sleep ≥ 7 hours

                    🤸‍♂️ Daily Triathlete (Combined): Single-day completion of Hydration Novice, Jogger, and Sleep Enthusiast
                    🏅 Weekly Triathlete (Combined): 7 consecutive days completing Hydration Novice, Jogger, and Sleep Enthusiast
                    👑 Ultimate Triathlete (Combined): 30 consecutive days completing Hydration Novice, Jogger, and Sleep Enthusiast
                    """.trimIndent()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAboutDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }        }
    }
}