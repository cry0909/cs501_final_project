// File: com/example/wellipet/ui/mobile/healthdata/HealthDataScreen.kt
package com.example.wellipet.ui.mobile.healthdata

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellipet.ui.components.CuteTopBar
import com.example.wellipet.ui.components.StepsLineChart
import com.example.wellipet.ui.components.SleepBarChart
import com.example.wellipet.ui.components.HydrationBarChart
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.wellipet.ui.components.CuteTopBar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun HealthDataScreen(onBackClick: () -> Unit) {
    val vm: HealthDataViewModel = viewModel()
    val steps by vm.currentSensorSteps.collectAsState()
    val sleepSec by vm.currentSleep.collectAsState()
    val hydration by vm.currentHydration.collectAsState()
    val formattedSleep = formatSleepDuration(sleepSec)
    val histSteps by vm.historicalSteps.collectAsState()
    val histSleep by vm.historicalSleep.collectAsState()
    val histHyd by vm.historicalHydration.collectAsState()

    val errorMessage by vm.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 每次 errorMessage 变更都会弹一次
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    // 範圍選擇
    var selectedDays by remember { mutableStateOf(7) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val daysOptions = listOf(7, 14, 30)
    var expandRange by remember { mutableStateOf(false) }
    // 在 resume 时、或天数变更时自动刷新
    LaunchedEffect(lifecycleOwner, selectedDays) {
        // 监听生命周期
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            vm.readHealthData(selectedDays)
        }
    }

    // 手動輸入水量
    var inputHyd by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CuteTopBar(
                title     = "Health Data",
                fontSize  = 22.sp,
                gradient  = Brush.horizontalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))),
                elevation = 4f,
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 日期範圍
            Card(
                colors    = cardColors(containerColor = Color(0xFFE6E1FC)),
                elevation = cardElevation(defaultElevation = 4.dp),
                shape     = RoundedCornerShape(12.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Selected Range: ", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { expandRange = true }) {
                        Text("$selectedDays Days")
                    }
                    DropdownMenu(
                        expanded = expandRange,
                        onDismissRequest = { expandRange = false }
                    ) {
                        daysOptions.forEach { d ->
                            DropdownMenuItem(
                                text = { Text("$d Days") },
                                onClick = {
                                    selectedDays = d
                                    expandRange = false
                                }
                            )
                        }
                    }
                }
            }

            // 2. 步數
            Card(
                colors    = cardColors(containerColor = Color(0xFFF0FCE7)),
                elevation = cardElevation(defaultElevation = 4.dp),
                shape     = RoundedCornerShape(12.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Steps (Past 24hrs)：$steps", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Steps History", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    StepsLineChart(data = histSteps, modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp))
                }
            }

            // 3. 睡眠
            Card(
                colors    = cardColors(containerColor = Color(0xFFFDEFE3)),
                elevation = cardElevation(defaultElevation = 4.dp),
                shape     = RoundedCornerShape(12.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Sleep (Past 24hrs)：$formattedSleep", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Sleep History", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    SleepBarChart(data = histSleep, modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp))
                }
            }

            // 4. 飲水
            Card(
                colors    = cardColors(containerColor = Color(0xFFE7FBFF)),
                elevation = cardElevation(defaultElevation = 4.dp),
                shape     = RoundedCornerShape(12.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Hydration (Past 24hrs)：$hydration ml", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    val target = 2000L
                    LinearProgressIndicator(
                        progress  = (hydration.toFloat()/target).coerceIn(0f,1f),
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value           = inputHyd,
                        onValueChange   = { inputHyd = it },
                        label           = { Text("Enter Hydration (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier        = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            inputHyd.toLongOrNull()?.let {
                                vm.addHydration(it)
                                inputHyd = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Hydration")
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Hydration History", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    HydrationBarChart(data = histHyd, modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp))
                }
            }
        }
    }
}



fun formatSleepDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return "${hours}hr ${minutes}min"
}
