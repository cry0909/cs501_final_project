package com.example.healthconnectdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 HealthConnectClient
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        setContent {
            HealthConnectDemoApp(healthConnectClient)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectDemoApp(healthConnectClient: HealthConnectClient) {
    var writeStatus by remember { mutableStateOf("Idle") }
    var readStatus by remember { mutableStateOf("Idle") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Health Connect Demo") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 寫入步數資料：直接傳入記錄清單
            Button(onClick = {
                (context as? ComponentActivity)?.lifecycleScope?.launch {
                    try {
                        val endTime = Instant.now()
                        val startTime = endTime.minus(Duration.ofMinutes(5))
                        val stepsRecord = StepsRecord(
                            count = 120,
                            startTime = startTime,
                            endTime = endTime,
                            startZoneOffset = ZoneOffset.UTC,
                            endZoneOffset = ZoneOffset.UTC,
                            metadata = Metadata.autoRecorded(
                                device = Device(type = Device.TYPE_WATCH)
                            )
                        )

                        healthConnectClient.insertRecords(listOf(stepsRecord))
                        writeStatus = "Write successful"
                    } catch (e: Exception) {
                        writeStatus = "Write failed: ${e.localizedMessage}"
                    }
                }
            }) {
                Text("Write Health Data")
            }
            Text("Write Status: $writeStatus")

            // 讀取最近 1 小時內的步數資料：假設 API 接受 startTime 與 endTime 參數
            Button(onClick = {
                (context as? ComponentActivity)?.lifecycleScope?.launch {
                    try {
                        val now = Instant.now()
                        val startTime = now.minus(Duration.ofHours(1))
                        val endTime = now

                        val response = healthConnectClient.readRecords(
                            ReadRecordsRequest(
                                StepsRecord::class,
                                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                        )
                        )
                        val totalSteps = response.records.sumOf { it.count }
                        readStatus = "Total steps in last hour: $totalSteps"
                    } catch (e: Exception) {
                        readStatus = "Read failed: ${e.localizedMessage}"
                    }
                }
            }) {
                Text("Read Health Data")
            }
            Text("Read Status: $readStatus")
        }
    }
}
