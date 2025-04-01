// File: com/example/wellipet/ui/mobile/healthdata/HealthDataScreen.kt
package com.example.wellipet.ui.mobile.healthdata

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.wellipet.data.repository.HealthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    // HealthRepository 需要 context，所以傳入 LocalContext.current
    val healthRepository = HealthRepository(context)
    var steps by remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()

    // 初次載入步數資料
    LaunchedEffect(Unit) {
        steps = healthRepository.getSteps()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WelliPet - Health Data") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // 以 Column 展示健康數據與臨時按鈕
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Health Data Overview (Charts / Lists in the future), Below is a temporary button to write steps data")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Total steps in last hour: $steps")
            Spacer(modifier = Modifier.height(16.dp))
            // 臨時按鈕：點擊後寫入步數資料，並更新顯示
            Button(onClick = {
                coroutineScope.launch {
                    // 寫入 100 步數據，成功後重新讀取步數資料
                    healthRepository.addSteps(100)
                    steps = healthRepository.getSteps()
                }
            }) {
                Text("Write Steps Data")
            }
        }
    }
}
