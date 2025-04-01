package com.example.wellipet.ui.mobile.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import com.example.wellipet.R
import com.example.wellipet.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("WelliPet - Home") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 背景圖片
            Image(
                painter = painterResource(id = R.drawable.bg_park),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // 寵物顯示區
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 寵物圖片（可替換為 GIF 或 Lottie 動畫）
                Image(
                    painter = painterResource(id = R.drawable.pet_dog),
                    contentDescription = "Pet",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                )
                Text("Feeling Happy", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daily Health progress: Steps / Sleep / Water ...")
            }
        }
    }
}
