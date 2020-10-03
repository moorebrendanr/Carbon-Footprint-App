package com.spice.carboncrushersrefactor.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.databinding.FragmentSummaryBinding
import com.spice.carboncrushersrefactor.viewmodels.SummaryViewModel
import kotlin.math.roundToInt

class SummaryFragment : Fragment() {
    private lateinit var binding: FragmentSummaryBinding
    private lateinit var pHelper: SharedPreferencesHelper
    private val viewModel by viewModels<SummaryViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSummaryBinding.inflate(layoutInflater)
        pHelper = SharedPreferencesHelper(requireContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))
        binding.chipWeek.isChecked = true
        setWeekUI()
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_week -> setWeekUI()
                R.id.chip_month -> setMonthUI()
                R.id.chip_year -> setYearUI()
            }
        }
        initializePieChart()
        if (savedInstanceState == null) {
            viewModel.fetchDailyLogsAndUpdate()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })
        viewModel.finishedFetchingData.observe(viewLifecycleOwner, Observer { finished ->
            if (finished) {
                Log.d("SummaryFragment", "observer")
                setData()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        val total: Int
        val trees: Int
        val estimate: Int
        val estimateTrees: Int
        val lastYearComparison: Int
        val lastYearTrees: Int
        when (viewModel.checkedChip) {
            0 -> {
                total = viewModel.pastWeekCarbonTotal.roundToInt()
                trees = total / 50
                estimate = (viewModel.yesterdayTotal * 7).roundToInt()
                estimateTrees = estimate / 50
                lastYearComparison = pHelper.avgCarbon / 52
                lastYearTrees = lastYearComparison / 50
            }
            1 -> {
                total = viewModel.pastMonthCarbonTotal.roundToInt()
                trees = total / 50
                estimate = (viewModel.pastWeekCarbonTotal * 4).roundToInt()
                estimateTrees = estimate / 50
                lastYearComparison = pHelper.avgCarbon / 12
                lastYearTrees = lastYearComparison / 50
            }
            2 -> {
                total = viewModel.pastYearCarbonTotal.roundToInt()
                trees = total / 50
                estimate = (viewModel.pastMonthCarbonTotal * 12).roundToInt()
                estimateTrees = estimate / 50
                lastYearComparison = pHelper.avgCarbon
                lastYearTrees = lastYearComparison / 50
            }
            else -> {
                total = 0
                trees = 0
                estimate = 0
                estimateTrees = 0
                lastYearComparison = 0
                lastYearTrees = 0
            }
        }
        val totalString = resources.getQuantityString(R.plurals.pounds_of_carbon, total, total)
        val treesString = resources.getQuantityString(R.plurals.trees, trees, trees)
        binding.textViewTotal.text = totalString
        binding.textViewTotalTrees.text = treesString
        binding.textViewEstimatedAmount.text =
                resources.getQuantityString(R.plurals.pounds_of_carbon, estimate, estimate)
        binding.textViewEstimatedTrees.text =
                resources.getQuantityString(R.plurals.trees, estimateTrees, estimateTrees)
        val lastYearTotalString =
                resources.getQuantityString(R.plurals.pounds_of_carbon, lastYearComparison, lastYearComparison)
        val lastYearTreesString =
                resources.getQuantityString(R.plurals.trees, lastYearTrees, lastYearTrees)
        binding.textViewComparisonPounds.text = "$totalString / $lastYearTotalString"
        binding.textViewComparisonTrees.text = "$treesString / $lastYearTreesString"
    }

    private fun setWeekUI() {
        viewModel.checkedChip = 0
        binding.textViewInReview.text = getString(R.string.in_review, "Week")
        binding.textViewEstimate.text = getString(R.string.estimate, "Week")
        setData()
    }

    private fun setMonthUI() {
        viewModel.checkedChip = 1
        binding.textViewInReview.text = getString(R.string.in_review, "Month")
        binding.textViewEstimate.text = getString(R.string.estimate, "Month")
        setData()
    }

    private fun setYearUI() {
        viewModel.checkedChip = 2
        binding.textViewInReview.text = getString(R.string.in_review, "Year")
        binding.textViewEstimate.text = getString(R.string.estimate, "Year")
        setData()
    }

    private fun initializePieChart() {
        val chart: PieChart = binding.piechart
        val yvalues = arrayListOf(
                PieEntry(18f, "Water Heating"),
                PieEntry(6f, "Air Conditioning"),
                PieEntry(41f, "Home Heating"),
                PieEntry(5f, "Refrigerator"),
                PieEntry(30f, "Electronics, lighting and other")
        )
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
    }
}