package com.example.presentation

import com.example.domain.model.TotalInfoData
import com.example.domain.repository.TotalInfoRepository
import com.example.domain.usecase.LoadTotalInfoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LoadTotalInfoUseCaseUnitTest {
    private lateinit var totalInfoRepository: TotalInfoRepository
    private lateinit var loadTotalInfoUseCase: LoadTotalInfoUseCase // 테스트 대상

    @BeforeEach
    fun setUp() {
        totalInfoRepository = mock()
        loadTotalInfoUseCase = LoadTotalInfoUseCase(totalInfoRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("DB 내 데이터가 존재하면 getTotalInfo() 호출 시 TotalDataInfo가 반환된다.")
    fun getTotalInfoFromDbWhenDataInDb() = runTest {
        // data 존재 -> getTotalInfo 호출 -> TotalInfoData 반환
        val username = "testUser"
        val mockData = TotalInfoData(username = username, date = "2025-01-22", content = "Sample Content", age = 28, personality = "긍정적")

        // getTotalInfo()는 성공한다고 가정
        whenever(totalInfoRepository.getTotalInfo(username)).thenReturn(mockData)

        val result = loadTotalInfoUseCase(username)

        // 결과 관련 검증
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty(), "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(totalInfoRepository).getTotalInfo(username)
        verify(totalInfoRepository, never()).fetchTotalInfo(any()) // 서버 호출하지 않아야 함
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("DB 내 데이터가 존재하지 않으면 getTotalInfo() 호출 시 null값이 반환되고, fetchTotalInfo()가 호출된다.")
    fun fetchTotalInfoFromServerWhenDataNotInDb() = runTest {
        // data 존재 x -> getTotalInfo 호출 -> null 반환 -> fetchTotalInfo 호출 -> TotalInfoData 반환
        val username = "testUser"
        val mockData = TotalInfoData(username = username, date = "2025-01-22", content = "Sample Content", age = 28, personality = "긍정적")

        // getTotalInfo() = null, fetchTotalInfo()는 성공한다고 가정
        whenever(totalInfoRepository.getTotalInfo(username)).thenReturn(null)
        whenever(totalInfoRepository.fetchTotalInfo(username)).thenReturn(mockData)

        val result = loadTotalInfoUseCase(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty(), "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(totalInfoRepository).getTotalInfo(any())
        verify(totalInfoRepository).fetchTotalInfo(username)
    }
}