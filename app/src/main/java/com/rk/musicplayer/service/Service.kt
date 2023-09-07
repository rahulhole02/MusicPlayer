package com.rk.musicplayer.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Service {
    private const val BASE_URL = "https://cms.samespace.com/items/"
    private val interceptor = HttpLoggingInterceptor()
    private val logLevel = interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    private val client: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(logLevel).build()
    private val retrofit: Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL)
            .client(client).addConverterFactory(GsonConverterFactory.create()).build()

    val retrofitApi: ApiInterface = retrofit.create(ApiInterface::class.java)
}