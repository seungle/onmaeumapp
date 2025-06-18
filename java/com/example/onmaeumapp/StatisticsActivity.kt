package com.example.onmaeumapp

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.AppDatabase
import com.example.onmaeumapp.data.model.EmotionType
import com.example.onmaeumapp.data.repository.EmotionRepositoryImpl
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class Period {
    WEEK, MONTH, YEAR
}

class StatisticsActivity : AppCompatActivity() {
    private lateinit var emotionRepository: EmotionRepository
    private lateinit var periodChipGroup: ChipGroup
    private lateinit var emotionChart: LineChart
    private lateinit var stressChart: BarChart
    private lateinit var meditationChart: PieChart

    private var selectedPeriod = Period.WEEK
    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        try {
            // Repository 초기화
            emotionRepository = EmotionRepositoryImpl(AppDatabase.getDatabase(this).emotionEntryDao())

            // UI 컴포넌트 초기화
            initializeViews()
            setupListeners()
            setupCharts()
            loadData()
        } catch (e: Exception) {
            Toast.makeText(this, "초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews() {
        try {
            periodChipGroup = findViewById(R.id.periodChipGroup)
            emotionChart = findViewById(R.id.emotionChart)
            stressChart = findViewById(R.id.stressChart)
            meditationChart = findViewById(R.id.meditationChart)

            // Toolbar 설정
            setSupportActionBar(findViewById(R.id.toolbar))
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.statistics_title)
        } catch (e: Exception) {
            throw IllegalStateException("UI 초기화 실패", e)
        }
    }

    private fun setupListeners() {
        periodChipGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPeriod = when (checkedId) {
                R.id.weekChip -> Period.WEEK
                R.id.monthChip -> Period.MONTH
                R.id.yearChip -> Period.YEAR
                else -> Period.WEEK
            }
            loadData()
        }
    }

    private fun setupCharts() {
        setupEmotionChart()
        setupStressChart()
        setupMeditationChart()
    }

    private fun setupEmotionChart() {
        emotionChart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
            }
            setDrawGridBackground(false)
            setDrawBorders(false)
            animateX(1000)
        }
    }

    private fun setupStressChart() {
        stressChart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
            }
            setDrawGridBackground(false)
            setDrawBorders(false)
            animateY(1000)
        }
    }

    private fun setupMeditationChart() {
        meditationChart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.WHITE)
            setDrawHoleEnabled(true)
            setHoleColor(Color.WHITE)
            setTransparentCircleRadius(30f)
            setHoleRadius(30f)
            setRotationAngle(0f)
            setRotationEnabled(true)
            animateY(1000)
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                val (startDate, endDate) = getDateRange()
                val entries = emotionRepository.getEntriesByDateRange(startDate, endDate)

                if (entries.isEmpty()) {
                    showEmptyDataMessage()
                } else {
                    updateEmotionChart(entries)
                    updateStressChart(entries)
                    updateMeditationChart(entries)
                }
            } catch (e: Exception) {
                Toast.makeText(this@StatisticsActivity, "데이터 로딩 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEmptyDataMessage() {
        Toast.makeText(this, "해당 기간의 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getDateRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time

        calendar.add(when (selectedPeriod) {
            Period.WEEK -> Calendar.WEEK_OF_YEAR
            Period.MONTH -> Calendar.MONTH
            Period.YEAR -> Calendar.YEAR
        }, -1)

        return Pair(calendar.time, endDate)
    }

    private fun updateEmotionChart(entries: List<EmotionEntry>) {
        try {
            val emotionValues = entries.groupBy { it.date }
                .mapValues { (_, entries) ->
                    entries.map { entry ->
                        when (entry.emotionType) {
                            EmotionType.HAPPY -> 5
                            EmotionType.CALM -> 4
                            EmotionType.NEUTRAL -> 3
                            EmotionType.ANXIOUS -> 2
                            EmotionType.SAD -> 1
                            EmotionType.ANGRY -> 0
                        }
                    }.average()
                }
                .toSortedMap()

            val dataPoints = emotionValues.map { (date, value) ->
                Entry(date.time.toFloat(), value.toFloat())
            }

            val dataSet = LineDataSet(dataPoints, getString(R.string.emotion_trend)).apply {
                color = getColor(R.color.primary)
                setCircleColor(getColor(R.color.primary))
                setDrawValues(false)
                lineWidth = 2f
                circleRadius = 4f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = getColor(R.color.primary)
                fillAlpha = 50
            }

            emotionChart.data = LineData(dataSet)
            emotionChart.xAxis.valueFormatter = IndexAxisValueFormatter(
                emotionValues.keys.map { dateFormat.format(it) }
            )
            emotionChart.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this, "감정 차트 업데이트 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStressChart(entries: List<EmotionEntry>) {
        try {
            val stressValues = entries.groupBy { it.date }
                .mapValues { (_, entries) ->
                    entries.map { it.stressLevel }.average()
                }
                .toSortedMap()

            val dataPoints = stressValues.map { (date, value) ->
                BarEntry(date.time.toFloat(), value.toFloat())
            }

            val dataSet = BarDataSet(dataPoints, getString(R.string.stress_level)).apply {
                color = getColor(R.color.error)
                setDrawValues(false)
                barBorderWidth = 1f
                barBorderColor = Color.WHITE
            }

            stressChart.data = BarData(dataSet)
            stressChart.xAxis.valueFormatter = IndexAxisValueFormatter(
                stressValues.keys.map { dateFormat.format(it) }
            )
            stressChart.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this, "스트레스 차트 업데이트 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMeditationChart(entries: List<EmotionEntry>) {
        try {
            val totalDays = when (selectedPeriod) {
                Period.WEEK -> 7
                Period.MONTH -> 30
                Period.YEAR -> 365
            }

            val completedDays = entries.map { it.date }
                .distinctBy { date ->
                    val calendar = Calendar.getInstance().apply { time = date }
                    calendar.get(Calendar.YEAR) * 10000 +
                    calendar.get(Calendar.MONTH) * 100 +
                    calendar.get(Calendar.DAY_OF_MONTH)
                }
                .size

            val dataPoints = listOf(
                PieEntry(completedDays.toFloat(), getString(R.string.completed)),
                PieEntry((totalDays - completedDays).toFloat(), getString(R.string.not_completed))
            )

            val dataSet = PieDataSet(dataPoints, getString(R.string.meditation_completion)).apply {
                colors = listOf(
                    getColor(R.color.success),
                    getColor(R.color.error)
                )
                valueTextSize = 12f
                valueTextColor = Color.WHITE
                valueFormatter = PercentFormatter(meditationChart)
                valueLinePart1Length = 0.4f
                valueLinePart2Length = 0.4f
                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            }

            meditationChart.data = PieData(dataSet)
            meditationChart.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this, "명상 차트 업데이트 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 차트 메모리 해제
        emotionChart.clear()
        stressChart.clear()
        meditationChart.clear()
    }
} 