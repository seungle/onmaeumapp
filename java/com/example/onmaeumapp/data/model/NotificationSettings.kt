package com.example.onmaeumapp.data.model

import java.time.LocalTime

/**
 * 알림 설정을 저장하는 데이터 클래스
 * @property isEnabled 알림 활성화 여부
 * @property time 알림 시간
 * @property days 알림을 받을 요일 (1: 월요일 ~ 7: 일요일)
 */
data class NotificationSettings(
    val isEnabled: Boolean,
    val time: LocalTime,
    val days: Set<Int>
) {
    init {
        require(days.all { it in 1..7 }) { "요일은 1(월요일)부터 7(일요일) 사이의 값이어야 합니다." }
    }
} 