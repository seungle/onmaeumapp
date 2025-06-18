package com.example.onmaeumapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.AppDatabase
import com.example.onmaeumapp.data.repository.EmotionRepositoryImpl
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DiaryDetailActivity : AppCompatActivity() {
    private lateinit var emotionRepository: EmotionRepository
    private var currentEntry: EmotionEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_detail)

        // Repository 초기화
        emotionRepository = EmotionRepositoryImpl(AppDatabase.getDatabase(this).emotionEntryDao())

        // Toolbar 설정
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.diary_detail_title)

        // Entry ID 가져오기
        val entryId = intent.getLongExtra(EXTRA_ENTRY_ID, -1)
        if (entryId != -1L) {
            loadEntry(entryId)
        } else {
            finish()
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
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        
        findViewById<TextView>(R.id.dateTextView).text = dateFormat.format(entry.date)
        findViewById<TextView>(R.id.emotionTextView).text = entry.emotionType.name
        findViewById<TextView>(R.id.contentTextView).text = entry.text
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_diary_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_edit -> {
                currentEntry?.let { entry ->
                    val intent = Intent(this, DiaryActivity::class.java).apply {
                        putExtra(EXTRA_ENTRY_ID, entry.id)
                    }
                    startActivity(intent)
                }
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_diary_title)
            .setMessage(R.string.delete_diary_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEntry()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteEntry() {
        currentEntry?.let { entry ->
            lifecycleScope.launch {
                emotionRepository.deleteEntry(entry)
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
    }
} 