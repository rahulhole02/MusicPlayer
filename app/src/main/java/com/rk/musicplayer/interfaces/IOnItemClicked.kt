package com.rk.musicplayer.interfaces

import com.rk.musicplayer.model.Songs

@FunctionalInterface
interface IOnItemClicked {
    fun onItemClicked(songList: List<Songs>, index: Int)
}