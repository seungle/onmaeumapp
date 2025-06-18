package com.example.onmaeumapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.model.EmotionType
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.repository.EmotionRepositoryImpl
import com.example.onmaeumapp.data.AppDatabase
import kotlinx.coroutines.launch
import java.util.Date

class DiaryActivity : AppCompatActivity() {
    private lateinit var emotionRepository: EmotionRepository
    private lateinit var emotionRadioGroup: RadioGroup
    private lateinit var contentEditText: EditText
    private var currentEntry: EmotionEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // Repository 초기화
        emotionRepository = EmotionRepositoryImpl(AppDatabase.getDatabase(this).emotionEntryDao())

        // UI 컴포넌트 초기화
        initializeViews()
        setupListeners()

        // Entry ID가 있으면 기존 데이터 로드
        val entryId = intent.getLongExtra(EXTRA_ENTRY_ID, -1)
        if (entryId != -1L) {
            loadEntry(entryId)
        }
    }

    private fun initializeViews() {
        emotionRadioGroup = findViewById(R.id.emotionRadioGroup)
        contentEditText = findViewById(R.id.contentEditText)

        // Toolbar 설정
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.diary_title)
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveEntry()
        }
    }

    private fun loadEntry(entryId: Long) {
        lifecycleScope.launch {
            emotionRepository.getEntryById(entryId)?.let { entry ->
                currentEntry = entry
                displayEntry(entry)
            }
        }
    }

    private fun displayEntry(entry: EmotionEntry) {
        // 감정 선택
        val emotionButtonId = when (entry.emotionType) {
            EmotionType.HAPPY -> R.id.radioHappy
            EmotionType.SAD -> R.id.radioSad
            EmotionType.ANGRY -> R.id.radioAngry
            EmotionType.ANXIOUS -> R.id.radioAnxious
            EmotionType.CALM -> R.id.radioCalm
            else -> R.id.radioCalm
        }
        emotionRadioGroup.check(emotionButtonId)

        // 내용 표시
        contentEditText.setText(entry.text)
    }

    private fun saveEntry() {
        val emotionType = when (emotionRadioGroup.checkedRadioButtonId) {
            R.id.radioHappy -> EmotionType.HAPPY
            R.id.radioSad -> EmotionType.SAD
            R.id.radioAngry -> EmotionType.ANGRY
            R.id.radioAnxious -> EmotionType.ANXIOUS
            R.id.radioCalm -> EmotionType.CALM
            else -> return
        }

        val content = contentEditText.text.toString()
        if (content.isBlank()) {
            contentEditText.error = getString(R.string.error_empty_content)
            return
        }

        val entry = currentEntry?.copy(
            emotionType = emotionType,
            content = content
        ) ?: EmotionEntry(
            date = Date(),
            emotionType = emotionType,
            stressLevel = 0,
            content = content,
            text = content

        )

        lifecycleScope.launch {
            emotionRepository.insertEntry(entry)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_diary, menu)
        return true
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

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
    }
} 