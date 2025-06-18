package com.example.onmaeumapp.data.repository

import android.content.Context
import com.example.onmaeumapp.data.model.NotificationSettings
import java.time.LocalTime

/**
 * 알림 설정을 관리하는 Repository 클래스
 * SharedPreferences를 사용하여 알림 설정을 저장하고 불러옵니다.
 */
class NotificationRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)

    /**
     * 저장된 알림 설정을 불러옵니다.
     * @return NotificationSettings 객체
     */
    fun getNotificationSettings(): NotificationSettings {
        return NotificationSettings(
            isEnabled = sharedPreferences.getBoolean("is_enabled", true),
            time = LocalTime.of(
                sharedPreferences.getInt("hour", 9),
                sharedPreferences.getInt("minute", 0)
            ),
            days = sharedPreferences.getStringSet("days", setOf("1", "2", "3", "4", "5", "6", "7"))?.map { it.toInt() }?.toSet() ?: setOf(1, 2, 3, 4, 5, 6, 7)
        )
    }

    /**
     * 알림 설정을 업데이트합니다.
     * @param isEnabled 알림 활성화 여부
     * @param time 알림 시간
     * @param days 알림을 받을 요일
     */
    fun updateNotificationSettings(isEnabled: Boolean, time: LocalTime, days: Set<Int>) {
        sharedPreferences.edit().apply {
            putBoolean("is_enabled", isEnabled)
            putInt("hour", time.hour)
            putInt("minute", time.minute)
            putStringSet("days", days.map { it.toString() }.toSet())
            apply()
        }
    }

    /**
     * 특정 유형의 알림 설정을 불러옵니다.
     * @param type 알림 유형 (meditation, diary, stress)
     * @return NotificationSettings 객체
     */
    fun getNotificationSettingsByType(type: String): NotificationSettings {
        return NotificationSettings(
            isEnabled = sharedPreferences.getBoolean("${type}_enabled", true),
            time = LocalTime.of(
                sharedPreferences.getInt("${type}_hour", 9),
                sharedPreferences.getInt("${type}_minute", 0)
            ),
            days = sharedPreferences.getStringSet("${type}_days", setOf("1", "2", "3", "4", "5", "6", "7"))?.map { it.toInt() }?.toSet() ?: setOf(1, 2, 3, 4, 5, 6, 7)
        )
    }

    /**
     * 특정 유형의 알림 설정을 업데이트합니다.
     * @param type 알림 유형 (meditation, diary, stress)
     * @param isEnabled 알림 활성화 여부
     * @param time 알림 시간
     * @param days 알림을 받을 요일
     */
    fun updateNotificationSettingsByType(type: String, isEnabled: Boolean, time: LocalTime, days: Set<Int>) {
        sharedPreferences.edit().apply {
            putBoolean("${type}_enabled", isEnabled)
            putInt("${type}_hour", time.hour)
            putInt("${type}_minute", time.minute)
            putStringSet("${type}_days", days.map { it.toString() }.toSet())
            apply()
        }
    }
} 