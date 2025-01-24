package com.example.presentation.state

import com.example.domain.model.TotalInfoData

data class MainHomeUiState(
    val isUsernameClear: Boolean = true,
    val isBtnSaveVisible: Boolean = true,
    val isBtnBackVisible: Boolean = false,
    val isTextFortuneVisible: Boolean = false,
    val totalInfoData: TotalInfoData
)
