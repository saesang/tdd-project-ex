package com.example.data.repositoryImpl

import android.util.Log
import androidx.room.PrimaryKey
import com.example.data.dto.FortuneInfoDto
import com.example.data.dto.UserInfoDto
import com.example.data.mapper.TotalInfoMapper
import com.example.data.retrofit.ServerApi
import com.example.data.room.TodayFortuneDb
import com.example.domain.model.TotalInfoData
import com.example.domain.repository.TotalInfoRepository
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response
import javax.inject.Inject

class TotalInfoRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val todayFortuneDb: TodayFortuneDb,
    private val mapper: TotalInfoMapper
) : TotalInfoRepository {
    override suspend fun getTotalInfo(username: String): TotalInfoData? {
        try {
            val totalInfoEntity =
                todayFortuneDb.dao().getTotalInfoByUsername(username).firstOrNull()

            return mapper.mapperToTotalInfoData(totalInfoEntity)
        } catch (e: RuntimeException) {
            when {
                e.message?.contains("DB 에러") == true -> {
                    //Log.e("TotalInfoRepositoryImpl", "DB 호출 에러 발생: ${e.message}")
                }

                else -> {
                    //Log.e("TotalInfoRepositoryImpl", "알 수 없는 RuntimeException: ${e.message}")
                }
            }
            return null
        }
    }

    override suspend fun fetchTotalInfo(username: String): TotalInfoData {
        lateinit var returnData: TotalInfoData
        val userInfo = safeApiCall({ serverApi.fetchUserInfo(username) }, "fetchUserInfo 실패")
        val fortuneInfo =
            safeApiCall({ serverApi.fetchFortuneInfo(username) }, "fetchFortuneInfo 실패")

        return if (userInfo != null && fortuneInfo != null) {
            val totalInfoEntity = mapper.mapperToTotalInfoEntity(fortuneInfo, userInfo)
            returnData = mapper.mapperToTotalInfoData(totalInfoEntity)!!
            try {
                todayFortuneDb.dao().insertTotalInfo(totalInfoEntity)
            } catch (e: RuntimeException) {
                return when {
                    e.message?.contains("DB 에러(저장 실패)") == true -> {
                        // Log.e("TotalInfoRepositoryImpl", "DB 호출 에러 발생: ${e.message}")
                        returnData
                    }

                    else -> {
                        //Log.e("TotalInfoRepositoryImpl", "알 수 없는 RuntimeException: ${e.message}")
                        TotalInfoData("", "", 0, "", "")
                    }
                }
            }
            returnData
        } else {
            TotalInfoData("", "", 0, "", "")
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()
            } else {
                // Log.e("TotalInfoRepositoryImpl", "HTTP 실패: $errorMessage")
                null
            }
        } catch (e: RuntimeException) {
            // Log.e("TotalInfoRepositoryImpl", "RuntimeException: $errorMessage")
            when {
                e.message?.contains("서버(fetchUserInfo) 에러") == true -> {
                    //Log.e("TotalInfoRepositoryImpl", "서버(fetchUserInfo) 에러 발생: ${e.message}")
                }

                e.message?.contains("서버(fetchFortuneInfo) 에러") == true -> {
                    //Log.e("TotalInfoRepositoryImpl", "서버(fetchFortuneInfo) 에러 발생: ${e.message}")
                }

                else -> {
                    //Log.e("TotalInfoRepositoryImpl", "알 수 없는 RuntimeException: ${e.message}")
                }
            }
            null
        }
    }
}