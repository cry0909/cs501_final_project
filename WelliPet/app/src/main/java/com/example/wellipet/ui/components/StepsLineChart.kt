// File: com/example/wellipet/ui/components/StepsLineChart.kt
package com.example.wellipet.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun StepsLineChart(
    data: List<Pair<String, Long>>, // Pair(dateString, step count)
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            LineChart(context).apply {
                // 關閉內建描述文字
                description.isEnabled = false
                // 隱藏右側軸
                axisRight.isEnabled = false
                // 啟用縮放、拖曳、手指放大
                setPinchZoom(true)
                setScaleEnabled(true)
                isDragEnabled = true
                // 預設無資料時的文字
                setNoDataText("No steps data available.")
                // x 軸設定（後續 update 區塊會設定 ValueFormatter）
                xAxis.apply {
                    granularity = 1f  // 每個間距 1
                    setLabelCount(data.size, true)
                    position = XAxis.XAxisPosition.BOTTOM
                }
                legend.isEnabled = false
            }
        },
        update = { chart ->
            // 將 data 轉換成 MPAndroidChart 需要的 Entry 集合
            val entries = data.mapIndexed { index, pair ->
                Entry(index.toFloat(), pair.second.toFloat())
            }
            val dataSet = LineDataSet(entries, "Steps History").apply {
                color = Color(245, 148, 110, 255).toArgb()
                setDrawCircles(true)
                circleRadius = 4f
                setCircleColor(Color(101, 31, 19, 255).toArgb())
                lineWidth = 2f
                valueTextSize = 10f
                setDrawValues(false)
            }
            chart.data = LineData(dataSet)

            // 設定 X 軸的 ValueFormatter，以顯示日期
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index in data.indices) {
                        // 這裡假設 date 字串格式為 "yyyy-MM-dd"，你也可以進一步格式化
                        data[index].first
                    } else ""
                }
            }
            chart.invalidate()
        }
    )
}
