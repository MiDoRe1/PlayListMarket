package com.example.playlistmarket.search.ui.activity

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarket.search.domain.models.Track

class TrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
): RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder = TrackViewHolder(parent) { position ->
        val track = tracks[position]
        onTrackClick(track)
    }


    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}