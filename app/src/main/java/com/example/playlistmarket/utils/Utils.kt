package com.example.playlistmarket.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import com.example.playlistmarket.ITunesSearchAPI.TrackInfo
import com.example.playlistmarket.R
import com.example.playlistmarket.TrackModel.Track
import java.util.Locale

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}

fun TrackInfo.toTrackModel(context: Context): Track {
    val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    val trackModel = Track(
        trackName = trackName ?: context.getString(R.string.unknown_track_name),
        artistName = artistName ?: context.getString(R.string.unknown_artist_name),
        trackTime = timeFormatter.format(trackTimeMillis ?: 0L),
        artworkUrl100 = artworkUrl100 ?: context.getString(R.string.unknown_track_url)
    )
    return trackModel
}