package com.example.todayfortunebytdd.di

import com.example.domain.repository.TotalInfoRepository
import com.example.domain.usecase.LoadTotalInfoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun loadTotalInfoUseCase(
        totalInfoRepository: TotalInfoRepository
    ): LoadTotalInfoUseCase {
        return LoadTotalInfoUseCase(totalInfoRepository)
    }
}