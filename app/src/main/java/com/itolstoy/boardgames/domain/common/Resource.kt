package com.itolstoy.boardgames.domain.common

sealed class Resource<out T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error(val message: String) : Resource<Nothing>()
    object NetworkError : Resource<Nothing>()
}