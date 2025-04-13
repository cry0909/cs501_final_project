//// File: com/example/wellipet/ui/components/StepsLineChart.kt
//package com.example.wellipet.ui.components
//
//import android.graphics.Color
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.viewinterop.AndroidView
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.components.Description
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//
//@Composable
//fun StepsLineChart(
//    data: List<Pair<String, Long>>,
//    modifier: Modifier = Modifier
//) {
//    // 使用 AndroidView 嵌入 MPAndroidChart 的 LineChart
//    val context = LocalContext.current
//    AndroidView(
//        modifier = modifier,
//        factory = {
//            // 建立 LineChart 實例
//            LineChart(context).apply {
//                // 關閉描述文字
//                description.isEnabled = false
//                // 如有需要，可自定義其他屬性
//                setNoDataText("No steps data available.")
//                axisRight.isEnabled = false
//                legend.isEnabled = false
//            }
//        },
//        update = { chart ->
//            // 將 data 轉換成 MPAndroidChart 需要的 Entry 集合
//            val entries = data.mapIndexed { index, pair ->
//                Entry(index.toFloat(), pair.second.toFloat())
//            }
//            // 建立 DataSet
//            val dataSet = LineDataSet(entries, "Steps History").apply {
//                color = Color.BLUE
//                setDrawCircles(true)
//                circleRadius = 4f
//                setCircleColor(Color.RED)
//                lineWidth = 2f
//                valueTextSize = 10f
//                setDrawValues(false)
//            }
//            // 建立圖表數據，並分配到 chart
//            chart.data = LineData(dataSet)
//            // 更新 X 軸標籤可以進一步設置 (例如用 pair.first 作為標籤)
//            chart.invalidate()
//        }
//    )
//}
