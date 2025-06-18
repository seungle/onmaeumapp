package com.example.onmaeumapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmaeumapp.backup.BackupFile
import com.example.onmaeumapp.backup.BackupManager
import com.example.onmaeumapp.data.AppDatabase
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.repository.EmotionRepositoryImpl
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BackupRestoreActivity : AppCompatActivity() {
    private lateinit var backupManager: BackupManager
    private lateinit var backupList: RecyclerView
    private lateinit var backupButton: MaterialButton
    private lateinit var adapter: BackupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_restore)

        val emotionRepository = EmotionRepositoryImpl(AppDatabase.getDatabase(this).emotionEntryDao())
        backupManager = BackupManager(this, emotionRepository)

        initializeViews()
        setupListeners()
        loadBackupList()
    }

    private fun initializeViews() {
        backupList = findViewById(R.id.backupList)
        backupButton = findViewById(R.id.backupButton)

        // Toolbar 설정
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.backup_restore)

        // RecyclerView 설정
        adapter = BackupAdapter(
            onRestore = { backupFile -> showRestoreConfirmationDialog(backupFile) },
            onDelete = { backupFile -> showDeleteConfirmationDialog(backupFile) }
        )
        backupList.layoutManager = LinearLayoutManager(this)
        backupList.adapter = adapter
    }

    private fun setupListeners() {
        backupButton.setOnClickListener {
            createBackup()
        }
    }

    private fun loadBackupList() {
        val backupFiles = backupManager.getBackupFiles()
        adapter.submitList(backupFiles)
    }

    private fun createBackup() {
        lifecycleScope.launch {
            backupButton.isEnabled = false
            try {
                val result = backupManager.createBackup()
                result.fold(
                    onSuccess = { path ->
                        Toast.makeText(
                            this@BackupRestoreActivity,
                            getString(R.string.backup_created),
                            Toast.LENGTH_SHORT
                        ).show()
                        loadBackupList()
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            this@BackupRestoreActivity,
                            getString(R.string.backup_failed, error.message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            } finally {
                backupButton.isEnabled = true
            }
        }
    }

    private fun showRestoreConfirmationDialog(backupFile: BackupFile) {
        AlertDialog.Builder(this)
            .setTitle(R.string.restore_confirmation_title)
            .setMessage(R.string.restore_confirmation_message)
            .setPositiveButton(R.string.restore) { _, _ ->
                restoreBackup(backupFile)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmationDialog(backupFile: BackupFile) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_confirmation_title)
            .setMessage(R.string.delete_confirmation_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteBackup(backupFile)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun restoreBackup(backupFile: BackupFile) {
        lifecycleScope.launch {
            try {
                val result = backupManager.restoreBackup(backupFile.path)
                result.fold(
                    onSuccess = {
                        Toast.makeText(
                            this@BackupRestoreActivity,
                            getString(R.string.restore_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            this@BackupRestoreActivity,
                            getString(R.string.restore_failed, error.message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@BackupRestoreActivity,
                    getString(R.string.restore_failed, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun deleteBackup(backupFile: BackupFile) {
        if (backupManager.deleteBackup(backupFile.path)) {
            Toast.makeText(
                this,
                getString(R.string.backup_deleted),
                Toast.LENGTH_SHORT
            ).show()
            loadBackupList()
        } else {
            Toast.makeText(
                this,
                getString(R.string.delete_failed),
                Toast.LENGTH_SHORT
            ).show()
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
}

class BackupAdapter(
    private val onRestore: (BackupFile) -> Unit,
    private val onDelete: (BackupFile) -> Unit
) : androidx.recyclerview.widget.ListAdapter<BackupFile, BackupAdapter.ViewHolder>(BackupDiffCallback()) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_backup, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val backupName: android.widget.TextView = itemView.findViewById(R.id.backupName)
        private val backupDate: android.widget.TextView = itemView.findViewById(R.id.backupDate)
        private val restoreButton: MaterialButton = itemView.findViewById(R.id.restoreButton)
        private val deleteButton: MaterialButton = itemView.findViewById(R.id.deleteButton)

        fun bind(backupFile: BackupFile) {
            backupName.text = backupFile.name
            backupDate.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(Date(backupFile.timestamp))

            restoreButton.setOnClickListener { onRestore(backupFile) }
            deleteButton.setOnClickListener { onDelete(backupFile) }
        }
    }
}

class BackupDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<BackupFile>() {
    override fun areItemsTheSame(oldItem: BackupFile, newItem: BackupFile): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: BackupFile, newItem: BackupFile): Boolean {
        return oldItem == newItem
    }
} 