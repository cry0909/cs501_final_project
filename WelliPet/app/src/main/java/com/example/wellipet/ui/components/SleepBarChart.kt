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
                // X-axis configuration: show a label for each entry
                xAxis.apply {
                    granularity = 1f
                    setLabelCount(data.size, true)
                    position = XAxis.XAxisPosition.BOTTOM
                }
            }
        },
        update = { chart ->
            // Convert data into BarEntry list, x-axis is index, y-axis is sleep duration (seconds to hours)
            val entries = data.mapIndexed { index, pair ->
                BarEntry(index.toFloat(), pair.second.toFloat() / 3600f)
            }
            // Create a BarDataSet
            val dataSet = BarDataSet(entries, "Sleep History").apply {
                // Determine bar color based on sleep hours:
                // ≥7hr: green; 5–7hr: orange; <5hr: red
                val colors = data.map { pair ->
                    val hours = pair.second.toFloat() / 3600f
                    when {
                        hours >= 7f -> androidx.compose.ui.graphics.Color(160, 232, 121, 255).toArgb()
                        hours in 5f..7f -> androidx.compose.ui.graphics.Color(241, 143, 56, 255).toArgb()
                        else -> androidx.compose.ui.graphics.Color(238, 54, 54, 255).toArgb()
                    }
                }
                setColors(*colors.toIntArray())
                valueTextSize = 10f
                // Format Y-axis values in hours, e.g.
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
            // Set X-axis labels to the date strings from the data
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
