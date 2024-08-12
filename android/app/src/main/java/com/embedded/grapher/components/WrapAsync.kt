package com.embedded.grapher.components

import androidx.compose.runtime.Composable
import com.embedded.grapher.utils.Async

@Composable
fun <T> WrapAsync(
    data: Async<T>,
    loading: @Composable (() -> Unit)? = null,
    error: @Composable ((error: Int) -> Unit)? = null,
    content: @Composable ((value: T) -> Unit)? = null
) {
    when (data) {
        is Async.Loading -> loading?.invoke()
        is Async.Error -> error?.invoke(data.errorMessage)
        is Async.Success -> content?.invoke(data.value)
    }
}