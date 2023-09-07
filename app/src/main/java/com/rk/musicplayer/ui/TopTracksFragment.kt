package com.rk.musicplayer.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rk.musicplayer.R
import com.rk.musicplayer.adapter.SongRecyclerViewAdapter
import com.rk.musicplayer.databinding.FragmentForYouBinding
import com.rk.musicplayer.databinding.FragmentTopTracksBinding
import com.rk.musicplayer.interfaces.IOnItemClicked
import com.rk.musicplayer.model.Songs

class TopTracksFragment(listener: IOnItemClicked) : Fragment(), IOnItemClicked {

    private var _binding: FragmentTopTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TabLayoutViewModel by activityViewModels()

    private var listener: IOnItemClicked
    /*companion object {
        fun newInstance() = TopTracksFragment()
    }*/
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
        //viewModel = ViewModelProvider(this)[TabLayoutViewModel::class.java]
        val filteredSongList = ArrayList<Songs>()
        val adapter = SongRecyclerViewAdapter(requireContext(), filteredSongList, this)
        binding.topTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.topTracksRecyclerView.setHasFixedSize(true)
        binding.topTracksRecyclerView.adapter = adapter
        viewModel._filteredTopTracks.observe(viewLifecycleOwner) {
            filteredSongList.clear()
            filteredSongList.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(songList: List<Songs>, index: Int) {
        listener.onItemClicked(songList, index)
    }
}