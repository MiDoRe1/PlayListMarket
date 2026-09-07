package com.example.playlistmarket.medialibrary.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaLibraryPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }

    override fun getItemCount(): Int = 2
}