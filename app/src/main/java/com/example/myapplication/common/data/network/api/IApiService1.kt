package com.example.myapplication.common.data.network.api

import com.example.myapplication.BuildConfig
import com.example.myapplication.common.data.network.api.IBaseService.Companion.getOkHttpClient
import com.example.myapplication.common.data.network.model.data_class_exmple
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface IApiService1 : IBaseService {

    @GET("products")
    suspend fun getUsers(): Response<List<data_class_exmple.data_class_exmpleItem>>

    companion object {
        fun getService(): IApiService1 {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BaseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .addConverterFactory(ScalarsConverterFactory.create())
                .build().create(IApiService1::class.java)
        }
    }
}
