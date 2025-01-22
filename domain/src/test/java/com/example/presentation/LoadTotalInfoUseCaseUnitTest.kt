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
    @DisplayName("DB 내 데이터가 존재하면, DB에서 해당 TotalInfoData를 반환한다.")
    fun should_GetTotalInfoFromDb_When_IsDataInDb() = runTest {
        // checkTotalInfoInDb 호출 -> true 반환 -> getTotalInfo 호출
        val username = "testUser"
        val mockData = TotalInfoData(username = username, date = "2025-01-22", content = "Sample Content", age = 28, personality = "긍정적")

        // isDataInDb = true, getTotalInfo()는 성공한다고 가정
        whenever(totalInfoRepository.checkTotalInfoInDb(username)).thenReturn(true)
        whenever(totalInfoRepository.getTotalInfo(username)).thenReturn(mockData)

        val result = loadTotalInfoUseCase(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty() == true, "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(totalInfoRepository).checkTotalInfoInDb(username)
        verify(totalInfoRepository).getTotalInfo(username)
        verify(totalInfoRepository, never()).fetchTotalInfo(any()) // 서버 호출하지 않아야 함
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("DB 내 데이터가 존재하지 않으면, 서버에서 TotalInfoData를 반환한다.")
    fun should_FetchTotalInfoFromServer_When_IsNotDataInDb() = runTest {
        // checkTotalInfoInDb 호출 -> false 반환 -> fetchTotalInfo 호출
        val username = "testUser"
        val mockData = TotalInfoData(username = username, date = "2025-01-22", content = "Sample Content", age = 28, personality = "긍정적")

        // isDataInDb = false, fetchTotalInfo()는 성공한다고 가정
        whenever(totalInfoRepository.checkTotalInfoInDb(username)).thenReturn(false)
        whenever(totalInfoRepository.fetchTotalInfo(username)).thenReturn(mockData)

        val result = loadTotalInfoUseCase(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty() == true, "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(totalInfoRepository).checkTotalInfoInDb(username)
        verify(totalInfoRepository, never()).getTotalInfo(any()) // DB 호출하지 않아야 함
        verify(totalInfoRepository).fetchTotalInfo(username)
    }
}