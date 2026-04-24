package com.focus.mob.ui.main
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.focus.mob.data.AppDatabase
import com.focus.mob.data.SessionRecord
import com.focus.mob.data.repository.AuthRepository
import com.focus.mob.data.repository.SessionRepository
import com.focus.mob.databinding.ActivityStatsBinding
import com.focus.mob.ui.viewmodel.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    private val sessionViewModel: SessionViewModel by viewModels()
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barChart = binding.barChart
        setupChart()

        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.STATS)
        observeViewModel()
        sessionViewModel.loadStats()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sessionViewModel.totalMinutes.collect { total ->
                        updateUI(total, sessionViewModel.sessions.value)
                    }
                }
                launch {
                    sessionViewModel.sessions.collect { sessions ->
                        updateUI(sessionViewModel.totalMinutes.value, sessions)
                    }
                }
            }
        }
    }

    private fun setupChart() {
        barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setScaleEnabled(false)
            setPinchZoom(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = ContextCompat.getColor(this@StatsActivity, R.color.text_secondary_dark)
                granularity = 1f
                axisLineColor = ContextCompat.getColor(this@StatsActivity, R.color.surface_dark)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(this@StatsActivity, R.color.surface_dark)
                textColor = ContextCompat.getColor(this@StatsActivity, R.color.text_secondary_dark)
                axisLineColor = ContextCompat.getColor(this@StatsActivity, R.color.surface_dark)
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
        }
    }

    private fun updateUI(totalMinutes: Int, sessions: List<SessionRecord>) {
        binding.tvTotalHours.text = (totalMinutes / 60).toString()
        binding.tvTotalMinutes.text = (totalMinutes % 60).toString()

        val minutesPerDay = FloatArray(7)
        val currentCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayStart = currentCal.timeInMillis
        val msPerDay = 24 * 60 * 60 * 1000L

        for (session in sessions) {
            val diff = todayStart - session.timestamp
            val daysAgo = if (diff < 0) 0 else (diff / msPerDay).toInt() + if (session.timestamp < todayStart) 1 else 0
            if (daysAgo in 0..6) {
                minutesPerDay[6 - daysAgo] += session.durationMinutes.toFloat()
            }
        }

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        val dayNames = arrayOf("S", "M", "T", "W", "T", "F", "S")
        val todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        for (i in 0..6) {
            entries.add(BarEntry(i.toFloat(), minutesPerDay[i]))
            var dayIndex = (todayDayOfWeek - 1 - (6 - i)) % 7
            if (dayIndex < 0) dayIndex += 7
            labels.add(dayNames[dayIndex])
        }

        val dataSet = BarDataSet(entries, "Focus Minutes").apply {
            color = ContextCompat.getColor(this@StatsActivity, R.color.primary)
            setDrawValues(false)
            highLightColor = ContextCompat.getColor(this@StatsActivity, R.color.white)
        }

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.data = BarData(dataSet).apply {
            barWidth = 0.6f
        }
        barChart.animateY(1000)
        barChart.invalidate()
    }
}
