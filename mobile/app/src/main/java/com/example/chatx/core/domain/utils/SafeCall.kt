package com.example.chatx.core.domain.utils

import com.example.chatx.core.presentation.utils.ExceptionUiText
import kotlin.coroutines.cancellation.CancellationException


suspend inline fun <reified T> safeCall(
    crossinline execute: suspend () -> T,
): Result<T, ErrorText> {
    return safeCall(
        onException = {e ->
            if(e is ExceptionUiText) return Result.Error(ErrorText(e.text))
            val error = e.localizedMessage?.let { UiText.Text(it) } ?: UiText.Text("Birşeyler yanlış gitti")
            Result.Error(ErrorText(error))
        },
        execute = execute
    )
}


suspend inline fun <reified T, E: Error> safeCall(
    crossinline execute: suspend () -> T,
    onException: ((e: Exception) -> Result<T, E>),
): Result<T, E> {
    return try {
        val result = execute()
        return Result.Success(result)
    }
    catch (e: CancellationException){
        throw e
    }
    catch (e: Exception){
        println("AppXXXX: errxx: $e")
        onException(e)
    }
}