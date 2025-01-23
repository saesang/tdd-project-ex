package com.example.data

import com.example.data.dto.FortuneInfoDto
import com.example.data.dto.UserInfoDto
import com.example.data.mapper.TotalInfoMapper
import com.example.data.retrofit.ServerApi
import com.example.data.room.TodayFortuneDb
import com.example.data.room.TotalInfoEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TotalInfoRepositoryImplUnitTest {
    private lateinit var serverApi: ServerApi
    private lateinit var todayFortuneDb: TodayFortuneDb
    private lateinit var mapper: TotalInfoMapper
    private lateinit var totalInfoRepositoryImpl: TotalInfoRepositoryImpl

    @BeforeEach
    fun setUp() {
        serverApi = mock()
        todayFortuneDb = mock()
        mapper = TotalInfoMapper
        totalInfoRepositoryImpl = TotalInfoRepositoryImpl(serverApi, todayFortuneDb)
    }

    /*
    테스트
    - fetchUserInfo() 호출 성공/에러
        - 성공: userInfo 반환
        - 에러 처리: emptyData 반환
    - fetchFortuneInfo() 호출 성공/에러
        - 성공: fortuneInfo 반환
        - 에러 처리: emptyData 반환
    - dao().getTotalInfoByUsername() 호출 성공/에러
        - 성공(db에 데이터 존재): TotalInfoEntity 반환
        - 성공(db에 데이터 존재 X): null 반환
        - 에러 처리: null 반환
    - dao().insertTotalInfo() 호출 성공/에러
        - 성공:
        - 에러 처리: Log 출력
     */

    @Test
    @DisplayName("dao().getTotalInfoByUsername() 호출 성공: DB 내 데이터가 존재하면, DB에서 해당 TotalInfoData를 반환한다.")
    fun getTotalInfoReturnsDataFromDbWhenDataInDb() = runTest {
        val username = "testUser"
        val mockData = TotalInfoEntity(username = username, date = "2025-01-22", content = "Sample Content", age = 28, personality = "긍정적")

        // dao().getTotalInfoByUsername(username) 성공
        whenever(todayFortuneDb.dao().getTotalInfoByUsername(username).firstOrNull()).thenReturn(mockData)

        val result = TotalInfoRepositoryImpl.getTotalInfo(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty(), "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(todayFortuneDb).dao().getTotalInfoByUsername(username)
    }

    @Test
    @DisplayName("dao().getTotalInfoByUsername() 호출 성공: DB 내 데이터가 존재하지 않으면, null을 반환한다.")
    fun getTotalInfoReturnsNullFromDbWhenDataNotInDb() = runTest {
        val username = "testUser"
        val mockData = null

        // dao().getTotalInfoByUsername(username) 성공
        whenever(todayFortuneDb.dao().getTotalInfoByUsername(username).firstOrNull()).thenReturn(mockData)

        val result = TotalInfoRepositoryImpl.getTotalInfo(username)

        // 결과 관련 검증
        assertNull(result)

        // 호출 관련 검증
        verify(todayFortuneDb).dao().getTotalInfoByUsername(username)
    }

    @Test
    @DisplayName("dao().getTotalInfoByUsername() 호출 실패: null을 반환한다.")
    fun getTotalInfoReturnsNullFromDbWhenDbFailure() = runTest {
        val username = "testUser"

        // dao().getTotalInfoByUsername(username) 실패
        whenever(todayFortuneDb.dao().getTotalInfoByUsername(username).firstOrNull()).thenThrow(RuntimeException("DB 에러"))

        val result = TotalInfoRepositoryImpl.getTotalInfo(username)

        // 결과 관련 검증
        assertNull(result)

        // 호출 관련 검증
        verify(todayFortuneDb).dao().getTotalInfoByUsername(username)
    }

    @Test
    @DisplayName("fetchUserInfo() 호출 성공, fetchFortuneInfo() 호출 성공, dao().insertTotalInfo() 호출 성공: 서버에서 TotalInfoData를 반환하고 DB에 저장한다.")
    fun fetchAndSaveTotalInfoFromServerWhenAllSucceed() = runTest {
        val username = "testUser"
        val (mockUserInfoDto, mockFortuneInfoDto) = createMockData()

        // fetchFortuneInfo(username), fetchUserInfo(username)은 성공한다고 가정
        whenever(serverApi.fetchUserInfo(username).body()!!).thenReturn(mockUserInfoDto)
        whenever(serverApi.fetchFortuneInfo(username).body()!!).thenReturn(mockFortuneInfoDto)
        val data = mapper.mapperToTotalInfoEntity(mockFortuneInfoDto, mockUserInfoDto)

        val result = TotalInfoRepositoryImpl.fetchTotalInfo(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty(), "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(serverApi).fetchUserInfo(username)
        verify(serverApi).fetchFortuneInfo(username)
        verify(mapper).mapperToTotalInfoEntity(mockFortuneInfoDto, mockUserInfoDto)
        verify(todayFortuneDb.dao()).insertTotalInfo(data)
    }

    private fun createMockData(): Pair<UserInfoDto, FortuneInfoDto> {
        val userInfo = UserInfoDto(username = "testUser", job = "programmer", age = 28, personality = "긍정적")
        val fortuneInfo = FortuneInfoDto(username = "testUser", date = "2025-01-22", content = "Sample Content", garbageData = "garb", garbageData2 = "garb")
        return Pair(userInfo, fortuneInfo)
    }

    @Test
    @DisplayName("fetchUserInfo() 호출 실패, fetchFortuneInfo() 호출 성공, dao().insertTotalInfo() 호출 실패: empty 값을 반환하고, DB 저장 로직을 호출하지 않는다.")
    fun fetchTotalInfoFromServerWithoutSavingWhenUserInfoFails() = runTest {
        val username = "testUser"
        val (mockUserInfoDto, mockFortuneInfoDto) = createMockData()

        // fetchFortuneInfo(username)만 성공한다고 가정
        whenever(serverApi.fetchUserInfo(username).body()!!).thenThrow(RuntimeException("서버(fetchUserInfo) 에러"))
        whenever(serverApi.fetchFortuneInfo(username).body()!!).thenReturn(mockFortuneInfoDto)

        val result = TotalInfoRepositoryImpl.fetchTotalInfo(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty(), "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(serverApi).fetchUserInfo(username)
        verify(serverApi).fetchFortuneInfo(username)
        verify(mapper).mapperToTotalInfoEntity(mockFortuneInfoDto, mockUserInfoDto)
        verify(todayFortuneDb.dao(), never()).insertTotalInfo(any()) // DB 저장 로직 호출되면 안됨
    }

    @Test
    @DisplayName("fetchUserInfo() 호출 성공, fetchFortuneInfo() 호출 실패, dao().insertTotalInfo() 호출 실패: empty 값을 반환하고, DB 저장 로직을 호출하지 않는다.")
    fun fetchTotalInfoFromServerWithoutSavingWhenFortuneInfoFails() = runTest {
        val username = "testUser"
        val (mockUserInfoDto, mockFortuneInfoDto) = createMockData()

        // fetchUserInfo(username)만 성공한다고 가정
        whenever(serverApi.fetchUserInfo(username).body()!!).thenReturn(mockUserInfoDto)
        whenever(serverApi.fetchFortuneInfo(username).body()!!).thenThrow(RuntimeException("서버(fetchFortuneInfo) 에러"))

        val result = TotalInfoRepositoryImpl.fetchTotalInfo(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty(), "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(serverApi).fetchUserInfo(username)
        verify(serverApi).fetchFortuneInfo(username)
        verify(mapper).mapperToTotalInfoEntity(mockFortuneInfoDto, mockUserInfoDto)
        verify(todayFortuneDb.dao(), never()).insertTotalInfo(any()) // DB 저장 로직 호출되면 안됨
    }

    @Test
    @DisplayName("fetchUserInfo() 호출 성공, fetchFortuneInfo() 호출 성공, dao().insertTotalInfo() 호출 실패: 서버에서 TotalInfoData를 반환하고, 저장 실패 log를 출력한다.")
    fun fetchTotalInfoFromServerWithoutSavingWhenSavingFails() = runTest {
        val username = "testUser"
        val (mockUserInfoDto, mockFortuneInfoDto) = createMockData()

        // fetchFortuneInfo(username), fetchUserInfo(username)은 성공한다고 가정
        whenever(serverApi.fetchUserInfo(username).body()!!).thenReturn(mockUserInfoDto)
        whenever(serverApi.fetchFortuneInfo(username).body()!!).thenReturn(mockFortuneInfoDto)
        val data = mapper.mapperToTotalInfoEntity(mockFortuneInfoDto, mockUserInfoDto)
        whenever(todayFortuneDb.dao().insertTotalInfo(data)).thenThrow(RuntimeException("DB 에러(저장 실패)"))

        val result = TotalInfoRepositoryImpl.fetchTotalInfo(username)

        // 결과 관련 검증
        assertNotNull(result)
        assertEquals(username, result.username)
        assertTrue(result.content.isNotEmpty(), "result.content는 빈 값이 아니다.")

        // 호출 관련 검증
        verify(serverApi).fetchUserInfo(username)
        verify(serverApi).fetchFortuneInfo(username)
        verify(mapper).mapperToTotalInfoEntity(mockFortuneInfoDto, mockUserInfoDto)
        verify(todayFortuneDb.dao()).insertTotalInfo(data)
    }
}