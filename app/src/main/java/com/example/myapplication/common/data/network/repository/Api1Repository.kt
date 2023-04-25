package com.example.myapplication.common.data.network.repository

import com.example.myapplication.common.data.network.api.IApiService1


class Api1Repository(private val apiService: IApiService1) : BaseRepository() {

    suspend fun getUsers(page: Int,pageSize: Int) = callApi { apiService.getUsers(page,pageSize) }


    companion object {
        @Volatile
        private var instance: Api1Repository? = null

        fun getInstance(): Api1Repository {

            return instance ?: synchronized(this) {
                instance ?: Api1Repository(IApiService1.getService())
                    .also {
                        instance = it
                    }
            }
        }
    }
}