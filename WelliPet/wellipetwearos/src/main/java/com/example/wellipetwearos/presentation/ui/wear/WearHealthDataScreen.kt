// File: com/example/wellipetwearos/ui/wear/WearHealthDataScreen.kt
package com.example.wellipetwearos.presentation.ui.wear

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


// 將秒數轉換為格式化字串，例如 "7hr 30min"
fun formatSleepDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return "${hours}hr ${minutes}min"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearHealthDataScreen() {
    // 模擬數據，你可以將此部分替換為實際從 Data Layer 收到的資料
    val simulatedSteps = remember { mutableStateOf(8000L) }
    val simulatedSleepSeconds = remember { mutableStateOf(27000L) }  // 27000秒約 7.5小時
    val simulatedHydration = remember { mutableStateOf(1500L) }       // 毫升

    // 使用 Scaffold 但不使用 TopAppBar，直接在內容中顯示標題
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 標題直接置中
                Text("Health Data", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Text("Steps (Past 24hr): ${simulatedSteps.value}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Text("Sleep: ${formatSleepDuration(simulatedSleepSeconds.value)}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Text("Hydration: ${simulatedHydration.value} ml",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
            }
        }
    )
}
