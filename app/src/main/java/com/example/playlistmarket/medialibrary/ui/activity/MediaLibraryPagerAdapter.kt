package com.example.playlistmarket.medialibrary.ui.activity

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmarket.medialibrary.ui.fragment.FavoriteTracksFragment
import com.example.playlistmarket.medialibrary.ui.fragment.PlaylistsFragment

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