package com.example.domain.repository

import com.example.domain.model.TotalInfoData

interface TotalInfoRepository {
    // DB에 데이터 있는지 확인: username이 같고 date가 오늘인 객체가 DB에 존재하면 true 반환
    suspend fun checkTotalInfoInDb(username: String): Boolean

    // DB 요청
    suspend fun getTotalInfo(username: String): TotalInfoData

    // 서버 요청
    suspend fun fetchTotalInfo(username: String): TotalInfoData
}