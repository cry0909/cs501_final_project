// File: com/example/wellipet/ui/components/HydrationBarChart.kt
package com.example.wellipet.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun HydrationBarChart(
    data: List<Pair<String, Long>>,  // List<Pair<dateString, hydration_ml>>
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
                xAxis.apply {
                    granularity = 1f
                    setLabelCount(data.size, true)
                    position = XAxis.XAxisPosition.BOTTOM
                }
            }
        },
        update = { chart ->
            // 將 data 轉換為 BarEntry 集合，x 為 index，y 為 hydration 值（毫升）
            val entries = data.mapIndexed { index, pair ->
                BarEntry(index.toFloat(), pair.second.toFloat())
            }
            // 根據每筆資料的飲水量決定顏色
            val colors = data.map { pair ->
                if (pair.second >= 2000L) {
                    // 淺藍：例如 0xFFADD8E6
                    Color(0xFFADD8E6).toArgb()
                } else {
                    // 黃色
                    Color.Yellow.toArgb()
                }
            }
            val dataSet = BarDataSet(entries, "Hydration History").apply {
                // 使用展開操作符 "*" 將 List<Int> 轉換為 vararg Int
                setColors(*colors.toIntArray())
                valueTextSize = 10f
                setDrawValues(true)
                // 格式化 Y 軸數值顯示，例如 "1500 ml"
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()} ml"
                    }
                }
            }
            chart.data = BarData(dataSet).apply {
                barWidth = 0.9f
            }
            // 設定 x 軸的 ValueFormatter：將每個 x 軸的值顯示對應的日期字串
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
