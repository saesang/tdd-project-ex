package com.example.presentation.state

import com.example.domain.model.TotalInfoData

data class MainHomeUiState(
    val isUsernameClear: Boolean,
    val isBtnSaveVisible: Boolean,
    val isBtnSaveEnabled: Boolean,
    val isBtnBackVisible: Boolean,
    val isTextFortuneVisible: Boolean,
    val totalInfoData: TotalInfoData?
)
