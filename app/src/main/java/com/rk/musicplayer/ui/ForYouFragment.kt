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
import com.rk.musicplayer.databinding.FragmentForYouBinding
import com.rk.musicplayer.interfaces.IOnItemClicked
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.util.Constants
import com.rk.musicplayer.util.Constants.Companion.NO_INTERNET

class ForYouFragment(listener: IOnItemClicked) : Fragment(), IOnItemClicked{

    private var _binding: FragmentForYouBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TabLayoutViewModel by activityViewModels()
    private var listener: IOnItemClicked
    /*companion object {
        fun newInstance() = ForYouFragment()
    }*/
    init {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForYouBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        val songList = ArrayList<Songs>()
        val adapter = SongRecyclerViewAdapter(requireContext(), songList, this)
        binding.songRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.songRecyclerView.setHasFixedSize(true)
        binding.songRecyclerView.adapter = adapter
        viewModel._songList.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.INVISIBLE
            songList.clear()
            songList.addAll(it)
            adapter.notifyDataSetChanged()
            if(it.isEmpty()){
                binding.songRecyclerView.visibility = View.INVISIBLE
                binding.songRecyclerView.visibility = View.VISIBLE
            } else {
                binding.songRecyclerView.visibility = View.VISIBLE
                binding.txtInfo.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(songList: List<Songs>, index: Int) {
        if(Constants.isInternetAvailable(requireContext())) {
            listener.onItemClicked(songList, index)
        } else {
            Snackbar.make(requireView(), NO_INTERNET, Snackbar.LENGTH_SHORT).show()
        }
    }
}