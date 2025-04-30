package com.example.wellipet.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun SleepBarChart(
    data: List<Pair<String, Long>>,  // List<Pair<dateString, sleepSeconds>>
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            BarChart(context).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                legend.isEnabled = false
                setPinchZoom(false)
                isDragEnabled = true
                // x 軸設定：每個間距顯示對應日期
                xAxis.apply {
                    granularity = 1f
                    setLabelCount(data.size, true)
                    position = XAxis.XAxisPosition.BOTTOM
                }
            }
        },
        update = { chart ->
            // 將 data 轉換成 BarEntry 集合, x 軸是 index, y 軸為睡眠時長 (秒轉換成小時)
            val entries = data.mapIndexed { index, pair ->
                BarEntry(index.toFloat(), pair.second.toFloat() / 3600f)
            }
            // 建立 BarDataSet
            val dataSet = BarDataSet(entries, "Sleep History").apply {
                // 根據睡眠時數 (以小時計) 決定顏色：
                // ≥7hr: 綠色, 5至7hr: 橘色, <5hr: 紅色
                val colors = data.map { pair ->
                    val hours = pair.second.toFloat() / 3600f
                    when {
                        hours >= 7f -> androidx.compose.ui.graphics.Color(160, 232, 121, 255).toArgb()
                        hours in 5f..7f -> androidx.compose.ui.graphics.Color(241, 143, 56, 255).toArgb()
                        else -> androidx.compose.ui.graphics.Color(238, 54, 54, 255).toArgb()
                    }
                }
                // 使用不帶 context 的 setColors() 方法
                setColors(*colors.toIntArray())
                valueTextSize = 10f
                // 格式化 Y 軸數值：以小時單位，例如 "7.5 hr"
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.1f hr", value)
                    }
                }
                setDrawValues(true)
            }
            chart.data = BarData(dataSet).apply {
                barWidth = 0.9f
            }
            // 設定 x 軸標籤，用資料中的日期字串來顯示
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index in data.indices) data[index].first else ""
                }
            }
            chart.invalidate()
        }
    )
}
