package com.example.playlistmarket.utils

import android.content.Context
import android.util.TypedValue
import com.example.playlistmarket.search.data.dto.TrackInfoDto
import com.example.playlistmarket.R
import com.example.playlistmarket.search.domain.models.Track
import java.util.Locale
import kotlin.String
import kotlin.random.Random
import java.text.SimpleDateFormat

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}

fun convertMillisecondsInNeededStringFormat(milliseconds: Long, format: String = "mm:ss"): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(milliseconds)
}

