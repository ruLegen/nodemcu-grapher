package com.embedded.grapher.utils


sealed class Async<out T> {
    object Loading : Async<Nothing>()

    data class Error(val errorMessage: Int) : Async<Nothing>()

    data class Success<out T>(val value: T) : Async<T>()
}

fun <T> Async<T>.isLoading() : Boolean{
    return  this is Async.Loading
}