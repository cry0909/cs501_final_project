// File: com/example/wellipet/ui/mobile/store/StoreScreen.kt
package com.example.wellipet.ui.mobile.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.wellipet.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellipet.ui.components.CuteTopBar

@Composable
fun StoreScreen(onBackClick: () -> Unit, storeViewModel: StoreViewModel = viewModel()) {
    // 寵物圖片資源列表
    val petImages = listOf(
        R.drawable.pet_dog,
        R.drawable.pet_cat,
        R.drawable.pet_rabbit
    )
    // 背景圖片資源列表
    val backgroundImages = listOf(
        R.drawable.bg_home,
        R.drawable.bg_park,
        R.drawable.bg_beach,
        R.drawable.bg_rain,
        R.drawable.bg_city,
        R.drawable.bg_avenue
    )
    // 資源 ID 列表，請替換成你自己 16 顆徽章的 R.drawable.xxx
    val allBadges = listOf(
        R.drawable.hydration_novice, R.drawable.hydration_expert, R.drawable.hydration_master, R.drawable.hydration_legend,
        R.drawable.step_beginner, R.drawable.jogger, R.drawable.step_sprinter, R.drawable.step_champion, R.drawable.step_legend,
        R.drawable.sleep_enthusiast, R.drawable.dream_weaver, R.drawable.sleep_master, R.drawable.sleep_legend,
        R.drawable.daily_triathlete, R.drawable.weekly_triathlete, R.drawable.ultimate_triathlete
    )

    val ctx = LocalContext.current

    // 從 StoreViewModel 中取得當前選擇 (DataStore 保存的值)
    val selectedPet by storeViewModel.selectedPet.collectAsState()
    val selectedBackground by storeViewModel.selectedBackground.collectAsState()
    val unlockedBadge by storeViewModel.unlockedBadges.collectAsState()
    val selectedBadge by storeViewModel.selectedBadges.collectAsState()

    Scaffold(
        topBar = {
            CuteTopBar(
                title     = "Store",
                fontSize  = 22.sp,
                gradient  = Brush.horizontalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))),
                elevation = 4f,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 寵物選擇區域
            Text("Select Your Pet", style = MaterialTheme.typography.titleLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(petImages) { petRes ->
                    Card(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { storeViewModel.selectPet(petRes) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = petRes),
                            contentDescription = "Pet Option",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // 背景選擇區域
            Text("Select Background", style = MaterialTheme.typography.titleLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(backgroundImages) { bgRes ->
                    Card(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { storeViewModel.selectBackground(bgRes) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = bgRes),
                            contentDescription = "Background Option",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            // --- 徽章選擇 ---
            Text("Choose Badges (Up to 3)", style = MaterialTheme.typography.titleLarge)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    // 大約顯示 3 列：80dp * 3 + 12dp * 2 = 264dp，再多點 padding
                    .height(360.dp)
            )  {
                items(allBadges) { badgeRes ->
                    // 將資源 ID 轉成唯一字串 ID，用於 toggleBadge
                    val badgeId = ctx.resources.getResourceEntryName(badgeRes)
                    val isUnlocked = unlockedBadge.contains(badgeId)
                    val isSelected = selectedBadge.contains(badgeId)

                    // 找灰階版 resource id
                    val grayRes = ctx.resources.getIdentifier(
                        "${badgeId}_locked", "drawable", ctx.packageName
                    )
                    // 決定顯示哪個圖
                    val displayRes = if (isUnlocked) badgeRes else grayRes

                    Card(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(enabled = isUnlocked){ storeViewModel.toggleBadge(badgeId) }
                            .then(
                                if (isSelected)
                                    Modifier.border(
                                        width = 3.dp,
                                        color = Color(0xFF6B3E1E),
                                        shape = CircleShape
                                    )
                                else Modifier
                            ),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = if (isSelected) CardDefaults.cardElevation(6.dp) else CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = displayRes),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(68.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
