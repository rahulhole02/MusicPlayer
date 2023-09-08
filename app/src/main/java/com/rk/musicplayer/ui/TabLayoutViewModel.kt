package com.rk.musicplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.service.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TabLayoutViewModel : ViewModel() {
    private val songList = MutableLiveData<List<Songs>>()
    val _songList: LiveData<List<Songs>> get() = songList
    private val filteredTopTracks = MutableLiveData<List<Songs>>()
    val _filteredTopTracks: LiveData<List<Songs>> get() = filteredTopTracks

    fun callApi(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = Service.retrofitApi.getSongsList()
            if(response.isSuccessful) {
                val responseBody = response.body()
                songList.postValue(responseBody?.data)
                filteredTopTracks.postValue(filterTopTrackList(responseBody?.data))
            } else {
                songList.postValue(ArrayList<Songs>())
                filteredTopTracks.postValue(filterTopTrackList(ArrayList<Songs>()))
            }
        }
    }

    private fun filterTopTrackList(data: List<Songs>?): List<Songs>? {
        return data?.filterNot {
            !it.top_track
        }
    }
}