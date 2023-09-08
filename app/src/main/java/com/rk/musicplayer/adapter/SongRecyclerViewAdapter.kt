package com.rk.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rk.musicplayer.R
import com.rk.musicplayer.interfaces.IOnItemClicked
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.util.Constants.Companion.COVERIMAGEURL

class SongRecyclerViewAdapter(private val context: Context,
                              private val songList: List<Songs>,
                              private val listener: IOnItemClicked
)
    : RecyclerView.Adapter<SongRecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val songName: TextView = itemView.findViewById(R.id.songName)
        val songArtist: TextView = itemView.findViewById(R.id.songArtist)
        val songImage: ImageView = itemView.findViewById(R.id.songImage)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.song_recyclerview_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songList[position]
        holder.songName.text = song.name
        holder.songArtist.text = song.artist
        Glide.with(context)
            .load(COVERIMAGEURL.plus(song.cover))
            .into(holder.songImage)
        holder.constraintLayout.setOnClickListener{
            listener.onItemClicked(songList, position)
        }
    }
}