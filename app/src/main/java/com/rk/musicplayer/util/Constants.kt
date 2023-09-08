package com.rk.musicplayer.util

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class Constants {
    fun View.snackbar(view: View, msg: String)  = Snackbar.make(view, msg, Toast.LENGTH_SHORT).show()
    companion object{
        const val SONG_PARCELABLE = "song_parcelable"
        const val CURRENT_INDEX = "current_index"
        const val COVERIMAGEURL = "https://cms.samespace.com/assets/"
        const val MSG_FOR_ERROR_IN_SONG = "Some issue with the song, enjoy next one"
        const val NO_INTERNET = "No internet available, please check network"

        fun isInternetAvailable(context: Context): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when{
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo ?: return false
                return networkInfo.isConnected
            }
        }
    }
}