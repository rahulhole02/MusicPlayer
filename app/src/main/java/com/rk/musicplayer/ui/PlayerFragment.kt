package com.rk.musicplayer.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.rk.musicplayer.R
import com.rk.musicplayer.databinding.FragmentPlayerBinding
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.util.Constants.Companion.COVERIMAGEURL
import com.rk.musicplayer.util.Constants.Companion.CURRENT_INDEX
import com.rk.musicplayer.util.Constants.Companion.SONG_PARCELABLE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var songList: List<Songs>
    private lateinit var song: Songs
    private var currentIndex: Int = 0

    //private lateinit var mDetector: GestureDetectorCompat
    companion object {
        fun newInstance() = PlayerFragment()
    }

    private lateinit var viewModel: PlayerViewModel
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekbarJob: Job
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        songList = arguments?.getParcelableArrayList(SONG_PARCELABLE)!!
        currentIndex = arguments?.getInt(CURRENT_INDEX)!!
        song = songList[currentIndex]
        //println("${song?.id} ${song?.name} ${song?.artist}")
        //mDetector = GestureDetectorCompat(requireContext(), MyGestureListener(findNavController()))
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        /*view.setOnTouchListener{_,event->
            mDetector.onTouchEvent(event)
        }*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        mediaPlayer = MediaPlayer()

        prepareMediaPlayer()
        binding.btnPlayPause.setImageResource(R.drawable.pause_icon)
        loadCover()

        binding.btnPlayPause.setOnClickListener{
            enablePlaying(mediaPlayer.isPlaying)
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
                    mediaPlayer.seekTo(progress)
                    binding.txtCurrentDuration.text = milliSecondsToTime(mediaPlayer.currentPosition.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        mediaPlayer.setOnBufferingUpdateListener { _, percent ->
            binding.songProgressBar.secondaryProgress = percent
        }

        mediaPlayer.setOnCompletionListener {
            binding.songProgressBar.progress = 0
            binding.btnPlayPause.setImageResource(R.drawable.play_icon)
            mediaPlayer.reset()
            prepareMediaPlayer()
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
        if(currentIndex > 0){
            song = songList[--currentIndex]
            binding.btnPrevious.isEnabled = currentIndex != 0
            binding.txtCurrentDuration.text = "0:00"
            binding.songProgressBar.progress = 0
            if(mediaPlayer.isPlaying){
                binding.btnPlayPause.setImageResource(R.drawable.play_icon)
            }
            loadCover()
            mediaPlayer.stop()
            mediaPlayer.reset()
            prepareMediaPlayer()
        }
    }

    private fun playNextSong() {
        if(currentIndex < songList.size - 1){
            song = songList[++currentIndex]
            binding.btnNext.isEnabled = currentIndex != songList.size - 1
            binding.txtCurrentDuration.text = "0:00"
            binding.songProgressBar.progress = 0
            if(mediaPlayer.isPlaying){
                binding.btnPlayPause.setImageResource(R.drawable.play_icon)
            }

            loadCover()
            mediaPlayer.stop()
            mediaPlayer.reset()
            prepareMediaPlayer()
        }
    }

    private fun enablePlaying(flag: Boolean){
        if(flag) {
            binding.btnPlayPause.setImageResource(R.drawable.play_icon)
            mediaPlayer.pause()
        } else {
            binding.btnPlayPause.setImageResource(R.drawable.pause_icon)
            //updateSeekbar()
        }
    }

    private fun prepareMediaPlayer(){
        try {
            binding.txtCurrentDuration.text = "0:00"
            binding.songProgressBar.progress = 0
            mediaPlayer.setDataSource(song.url)
            mediaPlayer.prepare()
            binding.songProgressBar.max = mediaPlayer.duration
            binding.txtTotalDuration.text = milliSecondsToTime(mediaPlayer.duration.toLong())
            mediaPlayer.start()
            updateSeekbar()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun updateSeekbar() {
        seekbarJob = lifecycleScope.launch {
            while(mediaPlayer.isPlaying){
                binding.songProgressBar.progress = mediaPlayer.currentPosition
                binding.txtCurrentDuration.text = milliSecondsToTime(mediaPlayer.currentPosition.toLong())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

/*
    class MyGestureListener(findNavController: NavController) : GestureDetector.SimpleOnGestureListener() {
        private val threshold = 100
        private val velocity = 100
        private var findNavController: NavController

        init {
            this.findNavController = findNavController
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val xDiff = e2.x - e1?.x!!
            val yDiff = e2.y - e1.y
            try {
                if(abs(xDiff) > abs(yDiff)) {
                    if(abs(xDiff) > threshold && abs(velocityX)> velocity){
                        if(xDiff > 0){
                            println("Swiped right")
                        } else{
                            println("Swiped left")
                        }
                        return true
                    }
                } else {
                    if(abs(yDiff) > threshold && abs(velocityY) > velocity) {
                        if(yDiff > 0) {
                            println("Swiped down")
                            findNavController.popBackStack()
                        } else {
                            println("Swiped up")
                        }
                        return true
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
*/

}