package com.rk.musicplayer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rk.musicplayer.interfaces.IOnItemClicked
import com.rk.musicplayer.ui.ForYouFragment
import com.rk.musicplayer.ui.TopTracksFragment

class MyViewPagerAdapter(activity: FragmentActivity, tabList: ArrayList<String>, listener: IOnItemClicked) : FragmentStateAdapter(activity) {
    private var tabList: ArrayList<String>
    private var listener: IOnItemClicked
    init {
        this.tabList = tabList
        this.listener = listener
    }
    override fun getItemCount(): Int {
        return tabList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ForYouFragment(listener)
            1 -> TopTracksFragment(listener)
            else -> ForYouFragment(listener)
        }
    }
}