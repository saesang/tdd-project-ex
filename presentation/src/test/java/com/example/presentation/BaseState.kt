package com.example.presentation

sealed class BaseState<out T>(val data : T?)  {
    companion object {
        val initial = BaseState.None
    }
    data object None: BaseState<Nothing>(data = null)
    data object Loading : BaseState<Nothing>(data = null)
    data class Error(val errorCode: Int) : BaseState<Nothing>(data = null)
    data class Success<out T>(val result : T) : BaseState<T>(data = result)
}