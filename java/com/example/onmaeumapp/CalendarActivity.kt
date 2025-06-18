package com.example.onmaeumapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.AppDatabase
import com.example.onmaeumapp.data.model.EmotionType
import com.example.onmaeumapp.data.repository.EmotionRepositoryImpl

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarActivity : AppCompatActivity() {
    private lateinit var emotionRepository: EmotionRepository
    private lateinit var monthYearTextView: TextView
    private lateinit var prevMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var diaryRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var diaryAdapter: DiaryAdapter

    private var currentDate: Calendar = Calendar.getInstance()
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Repository 초기화
        emotionRepository = EmotionRepositoryImpl(AppDatabase.getDatabase(this).emotionEntryDao())

        // UI 컴포넌트 초기화
        initializeViews()
        setupListeners()
        setupRecyclerViews()
        updateMonthYear()
        loadCalendarData()
    }

    private fun initializeViews() {
        monthYearTextView = findViewById(R.id.monthYearTextView)
        prevMonthButton = findViewById(R.id.prevMonthButton)
        nextMonthButton = findViewById(R.id.nextMonthButton)
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        diaryRecyclerView = findViewById(R.id.diaryRecyclerView)

        // Toolbar 설정
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.calendar_title)
    }

    private fun setupListeners() {
        prevMonthButton.setOnClickListener {
            currentDate.add(Calendar.MONTH, -1)
            updateMonthYear()
            loadCalendarData()
        }

        nextMonthButton.setOnClickListener {
            currentDate.add(Calendar.MONTH, 1)
            updateMonthYear()
            loadCalendarData()
        }
    }

    private fun setupRecyclerViews() {
        calendarAdapter = CalendarAdapter { date ->
            selectedDate = date
            loadDiaryEntries(date.time)
        }
        calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(this@CalendarActivity, 7)
            adapter = calendarAdapter
        }

        diaryAdapter = DiaryAdapter { entry ->
            val intent = Intent(this, DiaryDetailActivity::class.java).apply {
                putExtra(DiaryDetailActivity.EXTRA_ENTRY_ID, entry.id)
            }
            startActivityForResult(intent, REQUEST_DIARY_DETAIL)
        }
        diaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = diaryAdapter
        }
    }

    private fun updateMonthYear() {
        val dateFormat = SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
        monthYearTextView.text = dateFormat.format(currentDate.time)
    }

    private fun loadCalendarData() {
        lifecycleScope.launch {
            val startDate = Calendar.getInstance().apply {
                set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val endDate = Calendar.getInstance().apply {
                set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1, 0)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }

            val entries = emotionRepository.getEntriesByDateRange(startDate.time, endDate.time)
            calendarAdapter.submitList(generateCalendarDays(startDate, entries))
        }
    }

    private fun loadDiaryEntries(date: Date) {
        lifecycleScope.launch {
            val calendar = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val startDate = calendar.time
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endDate = calendar.time

            val entries = emotionRepository.getEntriesByDateRange(startDate, endDate)
            diaryAdapter.submitList(entries)
        }
    }

    private fun generateCalendarDays(startDate: Calendar, entries: List<EmotionEntry>): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = startDate.clone() as Calendar

        // 이전 달의 날짜들
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))

        // 6주치 날짜 생성
        repeat(42) {
            val date = calendar.time
            val entry = entries.find { entry ->
                val entryCalendar = Calendar.getInstance().apply { time = entry.date }
                entryCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                entryCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                entryCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
            }

            days.add(CalendarDay(
                date = calendar.time,
                day = calendar.get(Calendar.DAY_OF_MONTH),
                isCurrentMonth = calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH),
                emotionType = entry?.emotionType
            ))

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DIARY_DETAIL && resultCode == RESULT_OK) {
            loadCalendarData()
            loadDiaryEntries(selectedDate.time)
        }
    }

    companion object {
        private const val REQUEST_DIARY_DETAIL = 1001
    }
}

data class CalendarDay(
    val date: Date,
    val day: Int,
    val isCurrentMonth: Boolean,
    val emotionType: EmotionType?
)

class CalendarAdapter(
    private val onDayClick: (Calendar) -> Unit
) : androidx.recyclerview.widget.ListAdapter<CalendarDay, CalendarAdapter.CalendarViewHolder>(CalendarDiffCallback()) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CalendarViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view, onDayClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarViewHolder(
        itemView: View,
        private val onDayClick: (Calendar) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        private val emotionChip: com.google.android.material.chip.Chip = itemView.findViewById(R.id.emotionChip)

        fun bind(day: CalendarDay) {
            dayTextView.text = day.day.toString()
            dayTextView.alpha = if (day.isCurrentMonth) 1f else 0.3f

            if (day.emotionType != null) {
                emotionChip.visibility = View.VISIBLE
                emotionChip.text = when (day.emotionType) {
                    EmotionType.HAPPY -> itemView.context.getString(R.string.emotion_happy)
                    EmotionType.CALM -> itemView.context.getString(R.string.emotion_calm)
                    EmotionType.NEUTRAL -> itemView.context.getString(R.string.emotion_neutral)
                    EmotionType.ANXIOUS -> itemView.context.getString(R.string.emotion_anxious)
                    EmotionType.SAD -> itemView.context.getString(R.string.emotion_sad)
                    EmotionType.ANGRY -> itemView.context.getString(R.string.emotion_angry)
                }
                emotionChip.setChipBackgroundColorResource(when (day.emotionType) {
                    EmotionType.HAPPY -> R.color.emotion_happy
                    EmotionType.CALM -> R.color.emotion_calm
                    EmotionType.NEUTRAL -> R.color.emotion_neutral
                    EmotionType.ANXIOUS -> R.color.emotion_anxious
                    EmotionType.SAD -> R.color.emotion_sad
                    EmotionType.ANGRY -> R.color.emotion_angry
                })
            } else {
                emotionChip.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val calendar = Calendar.getInstance().apply { time = day.date }
                onDayClick(calendar)
            }
        }
    }
}

class CalendarDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<CalendarDay>() {
    override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }
} 