package com.example.onmaeumapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 사용자의 명상 기록을 나타내는 데이터 클래스
 * @property id 고유 식별자
 * @property date 명상을 수행한 날짜와 시간
 * @property meditationContentId 수행한 명상 콘텐츠의 ID
 * @property duration 실제 수행한 시간(분)
 * @property completed 완료 여부
 * @property notes 추가 메모
 */
@Entity(tableName = "meditation_entries")
data class MeditationEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,
    val meditationContentId: Long,
    val duration: Int,
    val completed: Boolean = false,
    val notes: String = ""
) {
    init {
        require(duration > 0) { "Duration must be greater than 0" }
    }
} 