package com.example.onmaeumapp

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.onmaeumapp.notification.NotificationManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class NotificationSettingsActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var meditationSwitch: SwitchMaterial
    private lateinit var diarySwitch: SwitchMaterial
    private lateinit var stressSwitch: SwitchMaterial
    private lateinit var meditationTimeEdit: TextInputEditText
    private lateinit var diaryTimeEdit: TextInputEditText

    private var meditationHour = 9
    private var meditationMinute = 0
    private var diaryHour = 21
    private var diaryMinute = 0

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        notificationManager = NotificationManager(this)

        initializeViews()
        setupListeners()
        loadSettings()
    }

    private fun initializeViews() {
        meditationSwitch = findViewById(R.id.meditationSwitch)
        diarySwitch = findViewById(R.id.diarySwitch)
        stressSwitch = findViewById(R.id.stressSwitch)
        meditationTimeEdit = findViewById(R.id.meditationTimeEdit)
        diaryTimeEdit = findViewById(R.id.diaryTimeEdit)

        // Toolbar 설정
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.notification_settings)
    }

    private fun setupListeners() {
        meditationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTimePickerDialog(true)
            } else {
                notificationManager.cancelAllNotifications()
            }
        }

        diarySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTimePickerDialog(false)
            } else {
                notificationManager.cancelAllNotifications()
            }
        }

        stressSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                notificationManager.scheduleStressCheck()
            } else {
                notificationManager.cancelAllNotifications()
            }
        }

        meditationTimeEdit.setOnClickListener {
            showTimePickerDialog(true)
        }

        diaryTimeEdit.setOnClickListener {
            showTimePickerDialog(false)
        }
    }

    private fun loadSettings() {
        // TODO: SharedPreferences에서 설정 불러오기
        meditationSwitch.isChecked = true
        diarySwitch.isChecked = true
        stressSwitch.isChecked = true

        updateTimeDisplay()
    }

    private fun showTimePickerDialog(isMeditation: Boolean) {
        val initialHour = if (isMeditation) meditationHour else diaryHour
        val initialMinute = if (isMeditation) meditationMinute else diaryMinute

        TimePickerDialog(
            this,
            { _, hour, minute ->
                if (isMeditation) {
                    meditationHour = hour
                    meditationMinute = minute
                    notificationManager.scheduleMeditationReminder(hour, minute)
                } else {
                    diaryHour = hour
                    diaryMinute = minute
                    notificationManager.scheduleDiaryReminder(hour, minute)
                }
                updateTimeDisplay()
            },
            initialHour,
            initialMinute,
            true
        ).show()
    }

    private fun updateTimeDisplay() {
        val meditationCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, meditationHour)
            set(Calendar.MINUTE, meditationMinute)
        }
        meditationTimeEdit.setText(timeFormat.format(meditationCalendar.time))

        val diaryCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, diaryHour)
            set(Calendar.MINUTE, diaryMinute)
        }
        diaryTimeEdit.setText(timeFormat.format(diaryCalendar.time))
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