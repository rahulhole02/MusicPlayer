package com.rk.musicplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize



data class SongsList(
    var data: List<Songs>
)

@Parcelize
data class Songs(
    var id: Int,
    var status: String = "",
    var sort: String?,
    var user_created: String = "",
    var date_created: String = "",
    var user_updated: String = "",
    var date_updated: String = "",
    var name: String = "",
    var artist: String = "",
    var accent: String = "",
    var cover: String = "",
    var top_track: Boolean,
    var url: String = ""
): Parcelable
