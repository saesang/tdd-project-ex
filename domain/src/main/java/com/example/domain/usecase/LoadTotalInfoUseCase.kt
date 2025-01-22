package com.example.domain.usecase

import com.example.domain.model.TotalInfoData
import com.example.domain.repository.TotalInfoRepository

class LoadTotalInfoUseCase(
    private val totalInfoRepository: TotalInfoRepository
) {
    suspend operator fun invoke(username: String): TotalInfoData {
        val isDataInDb = totalInfoRepository.checkTotalInfoInDb(username)

        return if (isDataInDb) {
            totalInfoRepository.getTotalInfo(username)
        } else {
            totalInfoRepository.fetchTotalInfo(username)
        }
    }
}