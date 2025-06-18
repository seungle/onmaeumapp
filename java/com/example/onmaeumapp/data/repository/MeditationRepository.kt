package com.example.onmaeumapp.data.repository

import com.example.onmaeumapp.data.dao.MeditationContentDao
import com.example.onmaeumapp.data.dao.MeditationEntryDao
import com.example.onmaeumapp.data.model.MeditationContent
import com.example.onmaeumapp.data.model.MeditationEntry
import kotlinx.coroutines.flow.Flow
import com.example.onmaeumapp.data.model.Meditation



interface MeditationRepository {
    fun getAllContents(): Flow<List<MeditationContent>>
    suspend fun getContentById(id: Long): MeditationContent?
    suspend fun insertContent(content: MeditationContent)
    suspend fun updateContent(content: MeditationContent)
    suspend fun deleteContent(content: MeditationContent)
    suspend fun getAllMeditations(): List<MeditationEntry>
    suspend fun insertMeditations(meditations: List<MeditationEntry>)
    suspend fun deleteAllMeditations()
    fun getContentsByCategory(category: String): Flow<List<MeditationContent>>
    suspend fun getMeditationByEmotion(emotion: EmotionType): Meditation
}

class MeditationRepositoryImpl(
    private val  meditationContentDao: MeditationContentDao,
    private val meditationEntryDao:MeditationEntryDao
) : MeditationRepository {
    override fun getAllContents(): Flow<List<MeditationContent>> = meditationContentDao.getAllContents()

    override suspend fun getContentById(id: Long): MeditationContent? = meditationContentDao.getContentById(id)

    override suspend fun insertContent(content: MeditationContent) = meditationContentDao.insertContent(content)

    override suspend fun updateContent(content: MeditationContent) = meditationContentDao.updateContent(content)

    override suspend fun deleteContent(content: MeditationContent) = meditationContentDao.deleteContent(content)

    override suspend fun getAllMeditations(): List<MeditationEntry> = meditationEntryDao.getAllMeditations()

    override suspend fun insertMeditations(meditations: List<MeditationEntry>) =
        meditationEntryDao.insertMeditations(meditations)

    override suspend fun deleteAllMeditations() = meditationEntryDao.deleteAllMeditations()

    override fun getContentsByCategory(category: String): Flow<List<MeditationContent>> =
        meditationContentDao.getContentsByCategory(category)

    override suspend fun getMeditationByEmotion(emotion: EmotionType): Meditation {
        // 예시 구현 코드
        return Meditation(
            title = "기본 명상",
            description = "감정에 따른 명상입니다.",
            duration = 10,
            category = "기본",
            audioUrl = "",
            difficulty = "초급",
            imageUrl = ""
        )
    }
} 