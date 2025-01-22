package com.example.domain.usecase

import com.example.domain.model.TotalInfoData
import com.example.domain.repository.TotalInfoRepository

class LoadTotalInfoUseCase(
    private val totalInfoRepository: TotalInfoRepository
) {
    suspend operator fun invoke(username: String): TotalInfoData {
        // 로직 수정: getTotalInfo 호출 -> 데이터 반환 but null 이면 fetchTotalInfo 호출
        return totalInfoRepository.getTotalInfo(username) ?: totalInfoRepository.fetchTotalInfo(username)
    }
}