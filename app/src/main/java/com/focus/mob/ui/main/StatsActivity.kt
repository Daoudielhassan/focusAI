package com.focus.mob.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.focus.mob.R
import com.focus.mob.databinding.ActivityStatsBinding
import com.focus.mob.domain.model.DailyFocusStat
import com.focus.mob.ui.viewmodel.StatsViewModel
import com.focus.mob.utils.NavigationUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private val viewModel: StatsViewModel by viewModels()
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barChart = binding.barChart
        setupChart()

        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.STATS)
        observeUiState()
    }

    // ═══════════════════════════════════════
    // STATE OBSERVATION
    // ═══════════════════════════════════════

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // ── Total focus time
                    binding.tvTotalHours.text   = (state.totalMinutes / 60).toString()
                    binding.tvTotalMinutes.text = (state.totalMinutes % 60).toString()

                    // ── Chart
                    if (state.weeklyFocus.isNotEmpty()) {
                        updateChart(state.weeklyFocus)
                    }
                }
            }
        }
    }

    // ═══════════════════════════════════════
    // CHART
    // ═══════════════════════════════════════

    private fun setupChart() {
        barChart.apply {
            description.isEnabled = false
            legend.isEnabled      = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setScaleEnabled(false)
            setPinchZoom(false)

            xAxis.apply {
                position      = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor     = ContextCompat.getColor(this@StatsActivity, R.color.text_secondary_dark)
                granularity   = 1f
                axisLineColor = ContextCompat.getColor(this@StatsActivity, R.color.surface_dark)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor     = ContextCompat.getColor(this@StatsActivity, R.color.surface_dark)
                textColor     = ContextCompat.getColor(this@StatsActivity, R.color.text_secondary_dark)
                axisLineColor = ContextCompat.getColor(this@StatsActivity, R.color.surface_dark)
                axisMinimum   = 0f
            }

            axisRight.isEnabled = false
        }
    }

    private fun updateChart(weeklyFocus: List<DailyFocusStat>) {
        val entries = weeklyFocus.mapIndexed { i, stat ->
            BarEntry(i.toFloat(), stat.minutes.toFloat())
        }
        val labels = weeklyFocus.map { it.dayLabel }

        val dataSet = BarDataSet(entries, "Focus Minutes").apply {
            color           = ContextCompat.getColor(this@StatsActivity, R.color.primary)
            highLightColor  = ContextCompat.getColor(this@StatsActivity, R.color.white)
            setDrawValues(false)
        }

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.data = BarData(dataSet).apply { barWidth = 0.6f }
        barChart.animateY(1000)
        barChart.invalidate()
    }
}
