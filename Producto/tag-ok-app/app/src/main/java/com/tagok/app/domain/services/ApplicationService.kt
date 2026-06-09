package com.tagok.app.domain.services

import android.util.Log
import com.tagok.app.data.remote.exceptions.ApiException
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.exceptions.ApplicationErrorMapper

abstract class ApplicationService
{
    protected suspend fun <T> execute(
        action: String,
        block: suspend () -> T): T
    {
        try
        {
            return block()
        }
        catch (e: ApplicationError)
        {
            Log.e("ApplicationService", "$action - ${e.message}", e)
            throw e
        }
        catch (e: ApiException)
        {
            Log.e("ApplicationService", "$action - API error", e)
            throw ApplicationErrorMapper.fromApiException(e)
        }
        catch (e: Exception)
        {
            Log.e("ApplicationService", "$action - Unexpected error", e)
            throw ApplicationError.Unknown
        }
    }
}