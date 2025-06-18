package com.example.onmaeumapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 1. 알림 채널 생성
        createNotificationChannel()

        // 2. 알림 권한 확인 및 요청
        checkAndRequestNotificationPermission()

        // 3. 알림 예약
        scheduleDiaryNotification()

        setupUI()
    }

    private fun setupUI() {
        val btnRecommend = findViewById<Button>(R.id.btnRecommend)
        val tvMeditation = findViewById<TextView>(R.id.tvMeditation)

        btnRecommend.setOnClickListener {
            try {
                val meditation = getString(R.string.meditation_recommendation)
                tvMeditation.text = meditation
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.error_loading_meditation), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 이미 있는 경우
                    scheduleDiaryNotification()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    // 권한이 거부된 경우 설명 표시
                    Toast.makeText(
                        this,
                        getString(R.string.notification_permission_rationale),
                        Toast.LENGTH_LONG
                    ).show()
                    requestNotificationPermission()
                }
                else -> {
                    // 처음 권한 요청
                    requestNotificationPermission()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NOTIFICATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scheduleDiaryNotification()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.notification_permission_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // 알림 예약 함수
    private fun scheduleDiaryNotification() {
        try {
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<DiaryNotificationWorker>(
                1, java.util.concurrent.TimeUnit.DAYS
            )
                .setConstraints(
                    androidx.work.Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
            androidx.work.WorkManager.getInstance(this).enqueue(workRequest)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.error_scheduling_notification),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // 알림 채널 생성 함수
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    "diary_channel",
                    getString(R.string.diary_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.diary_channel_description)
                }
                val manager = getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.error_creating_channel),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}