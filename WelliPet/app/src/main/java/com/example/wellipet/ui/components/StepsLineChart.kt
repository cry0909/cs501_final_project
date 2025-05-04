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
                // Disable the built-in description label
                description.isEnabled = false
                // Hide the right-hand Y-axis
                axisRight.isEnabled = false
                // Enable pinch zoom, scaling, and dragging
                setPinchZoom(true)
                setScaleEnabled(true)
                isDragEnabled = true
                // Default text when no data is available
                setNoDataText("No steps data available.")
                // X-axis configuration
                xAxis.apply {
                    granularity = 1f  // 每個間距 1
                    setLabelCount(data.size, true)
                    position = XAxis.XAxisPosition.BOTTOM
                }
                legend.isEnabled = false
            }
        },
        update = { chart ->
            // Convert data into MPAndroidChart Entry list
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

            // Set the X-axis ValueFormatter to display dates
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index in data.indices) {
                        // Assumes date string format "yyyy-MM-dd"
                        data[index].first
                    } else ""
                }
            }
            chart.invalidate()
        }
    )
}
