package com.example.playlistmarket.player.ui.fragment

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.PlaylistItemHorizontalBinding
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.utils.dpToPx
import com.example.playlistmarket.utils.getTracksPlural
import java.io.File

open class PlaylistItemHorizontalViewHolder(
    protected val binding: PlaylistItemHorizontalBinding,
    private val onClick: (position: Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) onClick(position)
        }
    }

    constructor(parent: ViewGroup, onClick: (position: Int) -> Unit): this(
        PlaylistItemHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onClick
    )

    fun bind(playlist: Playlist) {
        binding.textviewPlaylistName.text = playlist.name
        binding.textviewPlaylistCountTracks.text = getTracksPlural(playlist.countOfTracksIn)
        loadArtworkView(playlist.coverUrl)

    }

    private fun loadArtworkView(url: String) {
        val imgViewContext = binding.imageviewPlaylistCover.context
        val fileName = url.substringAfterLast("/")
        val file = File(File(imgViewContext.filesDir, "playlistCovers"), fileName)
        val pxValueOfRoundedCorner = dpToPx(
            getDpSizeForRoundedCornerOfTrackImage(),
            imgViewContext
        )
        Glide.with(binding.imageviewPlaylistCover)
            .load(file)
            .placeholder(R.drawable.img_track_placeholder)
            .error(R.drawable.img_track_placeholder)
            .fallback(R.drawable.img_track_placeholder)
            .transform(CenterCrop(), RoundedCorners(pxValueOfRoundedCorner))
            .into(binding.imageviewPlaylistCover)
    }
    private fun getDpSizeForRoundedCornerOfTrackImage(): Float {
        val outValue = TypedValue()
        binding.imageviewPlaylistCover.context.resources.getValue(
            R.dimen.default_rounded_corner_of_playlist_image,
            outValue,
            true
        )
        val dpValue = TypedValue.complexToFloat(outValue.data)
        return dpValue
    }

}