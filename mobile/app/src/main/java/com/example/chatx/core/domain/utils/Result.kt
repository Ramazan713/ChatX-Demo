package com.example.chatx.core.domain.utils

import androidx.annotation.StringRes


sealed interface Result<out D, out E: Error> {

    data class Success<out D>(val data: D): Result<D, Nothing>

    data class Error<out E: com.example.chatx.core.domain.utils.Error>(val error: E):
        Result<Nothing, E>

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val getSuccessData: D?
        get() = if (this is Success) this.data else null

    val getFailureError: E?
        get() = if (this is Error) this.error else null

    fun onSuccess(callback: (data: D) -> Unit){
        getSuccessData?.let(callback)
    }
    suspend fun onSuccessAsync(callback: suspend (data: D) -> Unit){
        getSuccessData?.let{ callback(it) }
    }
    fun onFailure(callback: (error: E) -> Unit){
        getFailureError?.let(callback)
    }

    companion object {
        fun errorWithUiText(uiText: UiText): Error<ErrorText> {
            return Error(ErrorText(uiText))
        }
        fun errorWithText(error: String): Error<ErrorText> {
            return errorWithUiText(UiText.Text(error))
        }

        fun errorWithResource(@StringRes resId: Int): Error<ErrorText> {
            return errorWithUiText(UiText.Resource(resId))
        }
    }

}

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this){
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E: Error> Result<T, E>.asEmptyResult(): EmptyResult<E> {
    return map {  }
}




typealias EmptyResult<E> = Result<Unit, E>

typealias DefaultResult<T> = Result<T, ErrorText>

typealias EmptyDefaultResult = Result<Unit, ErrorText>
