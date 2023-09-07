package com.rk.musicplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rk.musicplayer.adapter.SongRecyclerViewAdapter
import com.rk.musicplayer.databinding.FragmentForYouBinding
import com.rk.musicplayer.interfaces.IOnItemClicked
import com.rk.musicplayer.model.Songs

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
        //viewModel = ViewModelProvider(this)[TabLayoutViewModel::class.java]
        val songList = ArrayList<Songs>()
        val adapter = SongRecyclerViewAdapter(requireContext(), songList, this)
        binding.songRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.songRecyclerView.setHasFixedSize(true)
        binding.songRecyclerView.adapter = adapter
        viewModel._songList.observe(viewLifecycleOwner) {
            songList.clear()
            songList.addAll(it)
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