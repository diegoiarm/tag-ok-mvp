package com.tagok.app.data.remote.exceptions

class ApiException(
    message: String,
    val statusCode: Int = 0,
    val type: ApiErrorType = ApiErrorType.UNKNOWN,
    cause: Throwable? = null) : Exception(message, cause)