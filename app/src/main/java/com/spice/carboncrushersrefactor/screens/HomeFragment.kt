package com.spice.carboncrushersrefactor.screens

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.viewmodels.HomeViewModel
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment(), OnChartGestureListener, OnChartValueSelectedListener {
    private lateinit var lineChart: LineChart
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val pHelper = SharedPreferencesHelper(requireContext()
                .getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))
        lineChart = view.findViewById(R.id.linechart)
        viewModel.fetchDailyLogs()
        val average = view.findViewById<TextView>(R.id.average_carbon_emission)
        val trees = view.findViewById<TextView>(R.id.trees)
        var count = pHelper.avgCarbon / 50
        trees.text = resources.getQuantityString(R.plurals.trees, count, count)
        count = pHelper.avgCarbon
        average.text = resources.getQuantityString(R.plurals.pounds_of_carbon, count, count)
        val chart: PieChart = view.findViewById(R.id.piechart)
        val yvalues = ArrayList<PieEntry>()
        yvalues.add(PieEntry(18f, "Water Heating"))
        yvalues.add(PieEntry(6f, "Air Conditioning"))
        yvalues.add(PieEntry(41f, "Home Heating"))
        yvalues.add(PieEntry(5f, "Refrigerator"))
        yvalues.add(PieEntry(30f, "Electronics, lighting and other"))
        val dataSet = PieDataSet(yvalues, "")
        dataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        chart.setDrawEntryLabels(false)
        chart.setUsePercentValues(true)
        chart.data = data
        chart.description.text = ""
        chart.animateXY(1400, 1400)
        chart.legend.textSize = 13f
        chart.legend.isWordWrapEnabled = true

        // Create content description for pie chart
        val contentDesc = StringBuilder("Pie chart.\n")
        for (pieEntry in yvalues) {
            contentDesc.append(pieEntry.label).append(": ").append(pieEntry.value).append("%\n")
        }
        chart.contentDescription = contentDesc
        lineChart.onChartGestureListener = this
        lineChart.setOnChartValueSelectedListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.completedFetch.observe(viewLifecycleOwner, Observer { isCompleted ->
            if (isCompleted) {
                val set1 = LineDataSet(energyEfficient(), "Energy Efficient")
                set1.fillAlpha = 110
                set1.valueTextSize = 8f
                set1.color = Color.BLACK
                set1.setCircleColor(Color.BLACK)
                set1.lineWidth = 1f
                set1.circleRadius = 3f
                set1.setDrawCircleHole(false)

                val set2 = LineDataSet(average(), "Average")
                set2.fillAlpha = 110
                set2.valueTextSize = 8f
                set2.color = Color.GREEN
                set2.setCircleColor(Color.GREEN)
                set2.lineWidth = 1f
                set2.circleRadius = 3f
                set2.setDrawCircleHole(false)

                val set3 = LineDataSet(userAverage(viewModel.monthlyTotals), "My Average")
                set3.valueTextSize = 8f
                set3.fillAlpha = 110
                set3.color = Color.RED
                set3.setCircleColor(Color.RED)
                set3.lineWidth = 1f
                set3.circleRadius = 3f
                set3.setDrawCircleHole(false)

                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(set1)
                dataSets.add(set2)
                dataSets.add(set3)
                val dataVal = LineData(dataSets)
                lineChart.data = dataVal
                val xAxis = HashMap<Float, String?>()
                xAxis[1f] = "January"
                xAxis[2f] = "February"
                xAxis[3f] = "March"
                xAxis[4f] = "April"
                xAxis[5f] = "May"
                xAxis[6f] = "June"
                xAxis[7f] = "July"
                xAxis[8f] = "August"
                xAxis[9f] = "September"
                xAxis[10f] = "October"
                xAxis[11f] = "November"
                xAxis[12f] = "December"
                lineChart.isDragEnabled = true
                lineChart.setScaleEnabled(true)
                lineChart.description.text = ""
                val x = lineChart.xAxis
                lineChart.axisLeft.setDrawLabels(false)
                lineChart.axisRight.setDrawLabels(false)
                x.valueFormatter = IAxisValueFormatter { value: Float, _: AxisBase? ->
                    if (xAxis.containsKey(value)) {
                        return@IAxisValueFormatter xAxis[value]
                    } else {
                        return@IAxisValueFormatter value.toString()
                    }
                }
                lineChart.setVisibleXRangeMaximum(12f)
                lineChart.invalidate()
                lineChart.legend.textSize = 13f
                lineChart.legend.isWordWrapEnabled = true
                lineChart.viewPortHandler.setMaximumScaleX(1.7f)

                // Generate content description for line chart
                val contentDesc = StringBuilder("Line graph.\n")
                contentDesc.append("Energy Efficient:\n")
                for (entry in set1.values) {
                    contentDesc.append(xAxis[entry.x]).append(": ").append(entry.y).append("\n")
                }
                contentDesc.append("Average:\n")
                for (entry in set2.values) {
                    contentDesc.append(xAxis[entry.x]).append(": ").append(entry.y).append("\n")
                }
                contentDesc.append("My Average:\n")
                for (entry in set3.values) {
                    contentDesc.append(xAxis[entry.x]).append(": ").append(entry.y).append("\n")
                }
                lineChart.contentDescription = contentDesc
            }
        })
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun energyEfficient(): ArrayList<Entry> {
        val yVals = ArrayList<Entry>()
        yVals.add(Entry(1f,575f))
        yVals.add(Entry(2f,500f))
        yVals.add(Entry(3f, 450f))
        yVals.add(Entry(4f, 425f))
        yVals.add(Entry(5f, 400f))
        yVals.add(Entry(6f, 500f))
        yVals.add(Entry(7f, 650f))
        yVals.add(Entry(8f, 700f))
        yVals.add(Entry(9f, 575f))
        yVals.add(Entry(10f, 550f))
        yVals.add(Entry(11f, 435f))
        yVals.add(Entry(12f, 500f))
        return yVals
    }

    private fun average(): ArrayList<Entry> {
        val yVals = ArrayList<Entry>()
        yVals.add(Entry(1f, 900f))
        yVals.add(Entry(2f, 800f))
        yVals.add(Entry(3f, 700f))
        yVals.add(Entry(4f, 650f))
        yVals.add(Entry(5f, 625f))
        yVals.add(Entry(6f, 700f))
        yVals.add(Entry(7f, 900f))
        yVals.add(Entry(8f, 975f))
        yVals.add(Entry(9f, 825f))
        yVals.add(Entry(10f, 800f))
        yVals.add(Entry(11f, 650f))
        yVals.add(Entry(12f, 750f))
        return yVals
    }

    private fun userAverage(data: HashMap<Int, Float>): ArrayList<Entry> {
        val yVals = ArrayList<Entry>()
        for (i in 1..12) {
            if (data.containsKey(i)) {
                yVals.add(Entry(i.toFloat(), data[i]!!))
            }
        }
        return yVals
    }

    override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartGesture) {}
    override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartGesture) {
        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartGesture.SINGLE_TAP) // or highlightTouch(null) for callback to onNothingSelected(...)
            lineChart.highlightValues(null)
    }

    override fun onChartLongPressed(me: MotionEvent) {}
    override fun onChartDoubleTapped(me: MotionEvent) {}
    override fun onChartSingleTapped(me: MotionEvent) {}
    override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {}
    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {
        Log.d("scale", "$scaleX,$scaleY")
    }

    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
        Log.d("translate", "$dX,$dY")
    }

    override fun onValueSelected(e: Entry, h: Highlight) {}
    override fun onNothingSelected() {}
}