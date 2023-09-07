package com.rk.musicplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.rk.musicplayer.R
import com.rk.musicplayer.adapter.MyViewPagerAdapter
import com.rk.musicplayer.databinding.FragmentTabLayoutBinding
import com.rk.musicplayer.interfaces.IOnItemClicked
import com.rk.musicplayer.model.Songs
import com.rk.musicplayer.util.Constants.Companion.CURRENT_INDEX
import com.rk.musicplayer.util.Constants.Companion.SONG_PARCELABLE
import kotlinx.coroutines.launch

class TabLayoutFragment : Fragment(), IOnItemClicked {

    private val tabList = arrayListOf("For You", "Top Tracks")
    private var _binding: FragmentTabLayoutBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = TabLayoutFragment()
    }

    private val viewModel: TabLayoutViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTabLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = ViewModelProvider(this)[TabLayoutViewModel::class.java]
        val tabLayout = binding.tabView
        val viewPager = binding.viewPager
        viewPager.adapter = MyViewPagerAdapter(requireActivity(), tabList, this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()
        /*viewModel.itemClicked.observe(viewLifecycleOwner){
            val bundle = bundleOf(SONG_PARCELABLE to it)
            findNavController().navigate(R.id.action_tabLayoutFragment_to_playerFragment, bundle)
        }*/
    }

    private fun callApi() {
        viewModel.callApi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(songList: List<Songs>, index: Int) {
        val bundle = bundleOf(SONG_PARCELABLE to songList, CURRENT_INDEX to index)
        findNavController().navigate(R.id.action_tabLayoutFragment_to_playerFragment, bundle)
    }

}