package com.example.myapplication.common.data.network.api

import com.example.myapplication.BuildConfig
import com.example.myapplication.common.data.network.api.IBaseService.Companion.getOkHttpClient
import com.example.myapplication.common.data.network.model.UserListResponse
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiService1 : IBaseService {

//    @POST("change-lang")
//    suspend fun language(@Body body: Language): Response<LanguageResponse>

    @GET("passenger")
    suspend fun getUsers(@Query("page") page: Int, @Query("size") pageSize: Int): Response<UserListResponse>

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
