package com.example.onmaeumapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.AppDatabase
import com.example.onmaeumapp.data.repository.EmotionRepositoryImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class DiaryListActivity : AppCompatActivity() {
    private lateinit var emotionRepository: EmotionRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiaryAdapter
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        // Repository 초기화
        emotionRepository = EmotionRepositoryImpl(AppDatabase.getDatabase(this).emotionEntryDao())

        // UI 컴포넌트 초기화
        initializeViews()
        setupListeners()
        loadDiaryEntries()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)

        // RecyclerView 설정
        adapter = DiaryAdapter { entry ->
            openDiaryDetail(entry.id)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Toolbar 설정
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.diary_list_title)
    }

    private fun setupListeners() {
        fab.setOnClickListener {
            startActivity(Intent(this, DiaryActivity::class.java))
        }
    }

    private fun loadDiaryEntries() {
        lifecycleScope.launch {
            // 최근 30일 데이터 로드
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_MONTH, -30)
            val startDate = calendar.time

            emotionRepository.getEntriesBetweenDates(startDate, endDate)
                .collectLatest { entries ->
                    adapter.submitList(entries)
                }
        }
    }

    private fun openDiaryDetail(entryId: Long) {
        val intent = Intent(this, DiaryDetailActivity::class.java).apply {
            putExtra(DiaryDetailActivity.EXTRA_ENTRY_ID, entryId)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_diary_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_statistics -> {
                startActivity(Intent(this, StatisticsActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, NotificationSettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadDiaryEntries()
    }
}

class DiaryAdapter(
    private val onItemClick: (EmotionEntry) -> Unit
) : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    private var entries: List<EmotionEntry> = emptyList()

    fun submitList(newEntries: List<EmotionEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount() = entries.size

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: android.widget.TextView = itemView.findViewById(R.id.dateTextView)
        private val emotionTextView: android.widget.TextView = itemView.findViewById(R.id.emotionTextView)
        private val contentTextView: android.widget.TextView = itemView.findViewById(R.id.contentTextView)

        init {
            itemView.setOnClickListener {
                onItemClick(entries[adapterPosition])
            }
        }

        fun bind(entry: EmotionEntry) {
            val dateFormat = java.text.SimpleDateFormat("yyyy년 MM월 dd일", java.util.Locale.getDefault())
            dateTextView.text = dateFormat.format(entry.date)
            emotionTextView.text = entry.emotionType.name
            contentTextView.text = entry.text
        }
    }
} 