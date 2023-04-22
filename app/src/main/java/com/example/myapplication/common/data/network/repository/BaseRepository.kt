package com.example.myapplication.common.data.network.repository

import com.example.myapplication.common.data.ApiError
import com.example.myapplication.common.data.ApiEvent
import com.example.myapplication.common.data.ApiSuccess
import com.example.myapplication.common.data.network.model.ResponseCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

abstract class BaseRepository {

    protected suspend fun <T> callApi(apiCall: suspend () -> Response<T>): ApiEvent<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.code() == ResponseCode.OK.code)
                    withContext(Dispatchers.Main) { ApiSuccess(response.body()) }
                else
                    throw HttpException(response)
            } catch (e: Exception) {
                ApiError(e)
            }
        }
    }
}