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
            // Convert data into a list of BarEntry, x is index, y is hydration in mL
            val entries = data.mapIndexed { index, pair ->
                BarEntry(index.toFloat(), pair.second.toFloat())
            }
            // Choose bar color based on hydration amount
            val colors = data.map { pair ->
                if (pair.second >= 2000L) {
                    Color(0xFFADD8E6).toArgb()
                } else {
                    Color.Yellow.toArgb()
                }
            }
            val dataSet = BarDataSet(entries, "Hydration History").apply {
                setColors(*colors.toIntArray())
                valueTextSize = 10f
                setDrawValues(true)
                // Format Y-axis values
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()} ml"
                    }
                }
            }
            chart.data = BarData(dataSet).apply {
                barWidth = 0.9f
            }
            // Set a ValueFormatter on the X-axis to display the corresponding date string
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
