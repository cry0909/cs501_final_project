// File: com/example/wellipet/ui/mobile/store/StoreScreen.kt
package com.example.wellipet.ui.mobile.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.wellipet.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellipet.ui.mobile.store.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    // 從 StoreViewModel 中取得當前選擇 (DataStore 保存的值)
    val selectedPet by storeViewModel.selectedPet.collectAsState()
    val selectedBackground by storeViewModel.selectedBackground.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WelliPet - Store") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
            Text("Select Your Pet", style = MaterialTheme.typography.titleMedium)
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
            if (selectedPet != null) {
                Text(
                    text = "Selected Pet: $selectedPet",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 背景選擇區域
            Text("Select Background", style = MaterialTheme.typography.titleMedium)
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
            if (selectedBackground != null) {
                Text(
                    text = "Selected Background: $selectedBackground",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
