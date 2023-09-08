package com.rk.musicplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rk.musicplayer.adapter.SongRecyclerViewAdapter
import com.rk.musicplayer.databinding.FragmentTopTracksBinding
import com.rk.musicplayer.interfaces.IOnItemClicked
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.util.Constants
import com.rk.musicplayer.util.Constants.Companion.NO_INTERNET
import com.rk.musicplayer.util.Constants.Companion.isInternetAvailable

class TopTracksFragment(listener: IOnItemClicked) : Fragment(), IOnItemClicked {

    private var _binding: FragmentTopTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TabLayoutViewModel by activityViewModels()
    private var listener: IOnItemClicked
    init {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        val filteredSongList = ArrayList<Songs>()
        val adapter = SongRecyclerViewAdapter(requireContext(), filteredSongList, this)
        binding.topTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.topTracksRecyclerView.setHasFixedSize(true)
        binding.topTracksRecyclerView.adapter = adapter
        viewModel._filteredTopTracks.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.INVISIBLE
            filteredSongList.clear()
            filteredSongList.addAll(it)
            adapter.notifyDataSetChanged()
            if(it.isEmpty()){
                binding.topTracksRecyclerView.visibility = View.INVISIBLE
                binding.txtInfo.visibility = View.VISIBLE
            } else {
                binding.topTracksRecyclerView.visibility = View.VISIBLE
                binding.txtInfo.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(songList: List<Songs>, index: Int) {
        if(isInternetAvailable(requireContext())) {
            listener.onItemClicked(songList, index)
        } else {
            Snackbar.make(requireView(), Constants.NO_INTERNET, Snackbar.LENGTH_SHORT).show()
        }
    }
}