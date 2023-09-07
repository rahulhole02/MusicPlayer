package com.rk.musicplayer.service

import com.rk.musicplayer.model.SongsList
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("songs")
    suspend fun getSongsList(): Response<SongsList>
}