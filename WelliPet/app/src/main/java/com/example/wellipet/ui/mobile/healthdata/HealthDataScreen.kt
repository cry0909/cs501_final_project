// File: com/example/wellipet/ui/mobile/healthdata/HealthDataScreen.kt
package com.example.wellipet.ui.mobile.healthdata

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellipet.ui.components.StepsLineChart
import com.example.wellipet.ui.components.SleepBarChart
import com.example.wellipet.data.model.StepCount

// Placeholder for line chart (Steps)
@Composable
fun StepsLineChartPlaceholder(data: List<Pair<String, Long>>) {
    // 實際應用中可使用 Compose chart library 或 MPAndroidChart 轉換為 Compose
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Steps History (Last 7 Days)", style = MaterialTheme.typography.bodyLarge)
        data.forEach { (date, steps) ->
            Text("$date: $steps steps")
        }
    }
}

// Placeholder for bar chart (Sleep)
@Composable
fun SleepBarChartPlaceholder(data: List<Pair<String, Long>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Sleep History (Last 7 Days)", style = MaterialTheme.typography.bodyLarge)
        data.forEach { (date, seconds) ->
            Text("$date: ${formatSleepDuration(seconds)}")
        }
    }
}

// Placeholder for bar chart (Hydration)
@Composable
fun HydrationBarChartPlaceholder(data: List<Pair<String, Long>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Hydration History (Last 7 Days)", style = MaterialTheme.typography.bodyLarge)
        data.forEach { (date, ml) ->
            Text("$date: $ml ml")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataScreen(onBackClick: () -> Unit) {
    val healthDataViewModel: HealthDataViewModel = viewModel()
    val currentSteps by healthDataViewModel.currentSensorSteps.collectAsState()
    val currentSleepSeconds by healthDataViewModel.currentSleep.collectAsState()
    val currentHydration by healthDataViewModel.currentHydration.collectAsState()
    val formattedSleep = formatSleepDuration(currentSleepSeconds)
    val historicalSteps by healthDataViewModel.historicalSteps.collectAsState()
    val historicalSleep by healthDataViewModel.historicalSleep.collectAsState()
    val historicalHydration by healthDataViewModel.historicalHydration.collectAsState()

    // 日期範圍選擇部分
    var selectedDays by remember { mutableStateOf(7) }
    val dayOptions = listOf(7, 14, 30)
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDays) {
        healthDataViewModel.readHealthData(selectedDays)
    }

    // Hydration 手動輸入 state
    var hydrationInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WelliPet - Health Data") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // 使用 Column 展示感應器步數與歷史記錄
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ){
            // 在頁面最上方加入日期範圍選擇
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Select range: ", style = MaterialTheme.typography.bodyLarge)
                    Box {
                        Button(onClick = { expanded = true }) {
                            Text("$selectedDays Days")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            dayOptions.forEach { day ->
                                DropdownMenuItem(
                                    text = { Text("$day Days") },
                                    onClick = {
                                        selectedDays = day
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            // Steps Section
            item {
                Text("Steps", style = MaterialTheme.typography.headlineSmall)
                Text("Today's Steps: $currentSteps", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Steps History", style = MaterialTheme.typography.headlineSmall)
                StepsLineChart(data = historicalSteps, modifier = Modifier.fillMaxWidth().height(200.dp))
//                StepsLineChartPlaceholder(data = historicalSteps)
            }
            // Sleep Section
            item {
                Text("Sleep", style = MaterialTheme.typography.headlineSmall)
                Text("Total Sleep (Past 24hrs): $formattedSleep", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sleep History", style = MaterialTheme.typography.headlineSmall)
                SleepBarChart(data = historicalSleep, modifier = Modifier.fillMaxWidth().height(200.dp))
//                SleepBarChartPlaceholder(data = historicalSleep)
            }
            // Hydration Section
            item {
                Text("Hydration", style = MaterialTheme.typography.headlineSmall)
                Text("Total Hydration (Past 24hrs): $currentHydration ml", style = MaterialTheme.typography.bodyLarge)
                // 假設每日目標 2000 ml
                val hydrationTarget = 2000L
                LinearProgressIndicator(
                    progress = (currentHydration.toFloat() / hydrationTarget.toFloat()).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hydrationInput,
                    onValueChange = { hydrationInput = it },
                    label = { Text("Enter Hydration (ml)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        hydrationInput.toLongOrNull()?.let { ml ->
                            healthDataViewModel.addHydration(ml)
                            hydrationInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Hydration")
                }
                Spacer(modifier = Modifier.height(8.dp))
                HydrationBarChartPlaceholder(data = historicalHydration)
            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = { healthDataViewModel.storeSensorSteps() }) {
//                Text("Store Sensor Steps to DB")
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = { healthDataViewModel.loadHistoricalData() }) {
//                Text("Load Historical Data")
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("Historical Data: ", style = MaterialTheme.typography.headlineSmall)
//            for (stepCount in historicalSteps) {
//                Text("At ${stepCount.createdAt}: ${stepCount.steps} steps")
//            }
        }
    }
}

fun formatSleepDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return "${hours}hr ${minutes}min"
}
