package com.tagok.app.domain.exceptions

import com.tagok.app.data.remote.exceptions.ApiErrorType
import com.tagok.app.data.remote.exceptions.ApiException

object ApplicationErrorMapper
{
    fun fromApiException(e: ApiException): ApplicationError
    {
        return when
        {
            e.type == ApiErrorType.NETWORK -> ApplicationError.Network

            e.type == ApiErrorType.TIMEOUT -> ApplicationError.Timeout

            e.statusCode == 401 -> ApplicationError.Unauthorized

            e.statusCode == 403 -> ApplicationError.Forbidden

            e.statusCode == 404 -> ApplicationError.NotFound

            e.type == ApiErrorType.SERVER -> ApplicationError.Server

            else -> ApplicationError.Unknown
        }
    }
}