package com.example.playlistmarket.search.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmarket.search.domain.models.Track

open class TrackAdapter(
    protected val tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
): RecyclerView.Adapter<TrackViewHolder>() {

    protected val adapterOnTrackClickEnvelop = {position: Int ->
        onTrackClick(tracks[position])
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder = TrackViewHolder(parent, adapterOnTrackClickEnvelop)



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