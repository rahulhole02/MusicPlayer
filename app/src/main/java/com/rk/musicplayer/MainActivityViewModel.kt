package com.rk.musicplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.service.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val songList = MutableLiveData<List<Songs>>()
    val _songList: LiveData<List<Songs>> get() = songList

    fun callApi(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = Service.retrofitApi.getSongsList()
            if(response.isSuccessful) {
                val responseBody = response.body()
                songList.postValue(responseBody?.data)
                println("Response successful")
            } else {
                println("Response failure")
            }
        }
    }
}