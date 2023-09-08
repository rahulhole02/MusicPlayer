package com.rk.musicplayer.ui

import android.app.Dialog
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.rk.musicplayer.R
import com.rk.musicplayer.databinding.FragmentPlayerBinding
import com.rk.musicplayer.interfaces.ISwipeGesture
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.util.Constants.Companion.COVERIMAGEURL
import com.rk.musicplayer.util.Constants.Companion.CURRENT_INDEX
import com.rk.musicplayer.util.Constants.Companion.MSG_FOR_ERROR_IN_SONG
import com.rk.musicplayer.util.Constants.Companion.NO_INTERNET
import com.rk.musicplayer.util.Constants.Companion.SONG_PARCELABLE
import com.rk.musicplayer.util.Constants.Companion.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.abs

class PlayerFragment : Fragment(), ISwipeGesture {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var songList: List<Songs>
    private lateinit var song: Songs
    private var currentIndex: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var job1: Job? = null
    private val progressDialog: Dialog by lazy {
        showProgressDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().actionBar?.hide()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        songList = arguments?.getParcelableArrayList(SONG_PARCELABLE)!!
        currentIndex = arguments?.getInt(CURRENT_INDEX)!!
        song = songList[currentIndex]
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMusicPlayer()
        updatePlayerData()

        binding.btnPlayPause.setOnClickListener{
            if(isInternetAvailable(requireContext())) {
                if (mediaPlayer?.isPlaying == true) {
                    binding.btnPlayPause.setImageResource(R.drawable.play_icon)
                    mediaPlayer?.pause()
                } else {
                    binding.btnPlayPause.setImageResource(R.drawable.pause_icon)
                    mediaPlayer?.start()
                    updateSeekbar()
                }
            } else {
                Snackbar.make(requireView(), NO_INTERNET, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnPrevious.setOnClickListener {
            playPreviousSong()
        }

        binding.btnNext.setOnClickListener {
            playNextSong()
        }

        binding.songProgressBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    mediaPlayer?.seekTo(progress)
                    binding.txtCurrentDuration.text = mediaPlayer?.currentPosition?.toLong()
                        ?.let { milliSecondsToTime(it) }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        mediaPlayer?.setOnBufferingUpdateListener { _, percent ->
            binding.songProgressBar.secondaryProgress = percent
        }
    }

    private fun updatePlayerData() {
        lifecycleScope.launch(Dispatchers.Main){
            job1?.join()
            println("inside updatePlayerData")
            binding.songProgressBar.max = mediaPlayer?.duration!!
            binding.txtTotalDuration.text = mediaPlayer?.duration?.toLong()?.let {milliSecondsToTime(it)}
            progressDialog.dismiss()
            binding.btnPlayPause.setImageResource(R.drawable.pause_icon)
            updateSeekbar()
        }
    }

    private fun initMusicPlayer() {
        progressDialog.show()
        binding.txtCurrentDuration.text = "0:00"
        binding.songProgressBar.progress = 0
        loadCover()
        job1 = lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer?.stop()
                    mediaPlayer?.reset()
                } else {
                    mediaPlayer = MediaPlayer()

                }
                mediaPlayer?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(song.url)
                    prepare()
                    start()
                    setOnCompletionListener {
                        binding.btnPlayPause.setImageResource(R.drawable.play_icon)
                        playNextSong()
                    }
                    setOnErrorListener { mp, what, extra ->
                        Toast.makeText(requireContext(), MSG_FOR_ERROR_IN_SONG, Toast.LENGTH_SHORT)
                            .show()
                        binding.btnPlayPause.setImageResource(R.drawable.play_icon)
                        mediaPlayer?.reset()
                        true
                    }
                }

            } catch(e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    binding.btnPlayPause.setImageResource(R.drawable.play_icon)
                    Snackbar.make(binding.root, MSG_FOR_ERROR_IN_SONG, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadCover() {
        binding.songName.text = song.name
        binding.songArtist.text = song.artist
        Glide.with(requireContext())
            .load(COVERIMAGEURL.plus(song.cover))
            .into(binding.imageView)
    }

    private fun playPreviousSong() {
        if(isInternetAvailable(requireContext())) {
            if (currentIndex >= 0) {
                if (currentIndex == 0)
                    currentIndex = songList.size
                song = songList[--currentIndex]
                initMusicPlayer()
                updatePlayerData()
            }
        } else {
            Snackbar.make(requireView(), NO_INTERNET, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun playNextSong() {
        if(isInternetAvailable(requireContext())) {
            if (currentIndex <= songList.size - 1) {
                if (currentIndex == songList.size - 1)
                    currentIndex = -1
                song = songList[++currentIndex]
                initMusicPlayer()
                updatePlayerData()
            }
        } else {
            Snackbar.make(requireView(), NO_INTERNET, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateSeekbar() {
        lifecycleScope.launch{
            while (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                println(mediaPlayer?.currentPosition)
                binding.songProgressBar.progress = mediaPlayer?.currentPosition!!
                binding.txtCurrentDuration.text = mediaPlayer?.currentPosition?.toLong()
                    ?.let { milliSecondsToTime(it) }
                delay(1000)
            }
        }
    }

    private fun milliSecondsToTime(milliSeconds: Long): String{
        var timerString = ""
        var secondString = ""
        val hours = milliSeconds/(1000*60*60)
        val minutes = (milliSeconds%(1000*60*60))/(1000*60)
        val seconds = (((milliSeconds%(1000*60*60)))%(1000*60)/1000)
        if(hours>0){
            timerString = hours.toString().plus(":")
        }
        secondString = if(seconds<10){
            "0".plus(seconds)
        } else {
            "".plus(seconds)
        }
        timerString = timerString.plus(minutes).plus(":").plus(secondString)
        return timerString
    }
    private fun showProgressDialog(): Dialog {
        val dialog = Dialog(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fragment_dialog)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
    }

    override fun swipeLeft() {
        playNextSong()
    }

    override fun swipeRight() {
        playPreviousSong()
    }
}