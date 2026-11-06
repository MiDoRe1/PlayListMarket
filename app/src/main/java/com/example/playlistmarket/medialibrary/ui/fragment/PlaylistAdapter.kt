package com.example.playlistmarket.medialibrary.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarket.R
import com.example.playlistmarket.medialibrary.domain.models.Playlist


class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onItemClick: (playList: Playlist) -> Unit
): RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            parent,
            {
                position: Int ->
                {
                    onItemClick(playlists[position])
                }

            }
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }
}