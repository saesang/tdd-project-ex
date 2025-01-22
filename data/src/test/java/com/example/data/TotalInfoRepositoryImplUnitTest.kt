package com.example.data

import com.example.data.retrofit.ServerApi
import com.example.data.room.TodayFortuneDb
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock


class TotalInfoRepositoryImplUnitTest {
    private lateinit var serverApi: ServerApi
    private lateinit var todayFortuneDb: TodayFortuneDb
    private lateinit var totalInfoRepositoryImpl: TotalInfoRepositoryImpl

    @BeforeEach
    fun setUp() {
        serverApi = mock()
        todayFortuneDb = mock()
        totalInfoRepositoryImpl = TotalInfoRepositoryImpl(serverApi, todayFortuneDb)
    }

    @Test
    @DisplayName("DB 내 데이터가 존재하면, DB에서 해당 TotalInfoData를 반환한다.")
    fun should_GetTotalInfoFromDb_When_IsDataInDb() = runTest {

    }

    @Test
    @DisplayName("DB 내 데이터가 존재하지 않으면, null을 반환한다.")
    fun should_GetNullFromDb_When_IsNotDataInDb() = runTest {

    }

    @Test
    @DisplayName("서버에서 TotalInfoData를 반환하고 DB에 저장한다.")
    fun should_FetchTotalInfoFromServer_When_() = runTest {

    }
}