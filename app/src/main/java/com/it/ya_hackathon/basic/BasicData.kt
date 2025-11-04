package com.it.ya_hackathon.basic

interface BasicUiState
interface BasicIntent
interface BasicEvent
interface BasicUiMessageIntent

sealed interface BasicFunResponse {
    object Success : BasicFunResponse
    class Error(val msg: String) : BasicFunResponse
}

sealed interface BasicResultFunResponse<out T> {
    data class Success<out T>(val data: T) : BasicResultFunResponse<T>
    data class Error(val msg: String) : BasicResultFunResponse<Nothing>
}
