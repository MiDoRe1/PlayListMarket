package com.example.playlistmarket

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.TrackModel.Track
import com.example.playlistmarket.utils.dpToPx
import com.google.gson.Gson

class MusicPlayActivity : AppCompatActivity() {

    private lateinit var currentTrack: Track
    private lateinit var imgTrackIcon : ImageView
    private lateinit var textViewTrackName : TextView
    private lateinit var textViewGroupName : TextView
    private lateinit var textViewTrackDurationValue : TextView
    private lateinit var textViewTrackAlbumValue : TextView
    private lateinit var textViewTrackYearValue : TextView
    private lateinit var textViewTrackGenreValue : TextView
    private lateinit var textViewTrackCountryValue : TextView
    private lateinit var buttonReturn : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_play)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentTrack = Gson().fromJson<Track>(
            intent.getStringExtra(JSON_FORMAT_TRACK_KEY),
            Track::class.java)
        initTrackIcon()
        initTrackInformation()
        initButtonReturn()
    }

    private fun initTrackIcon() {
        imgTrackIcon = findViewById(R.id.imgTrackIcon)
        val roundedCornerValueInPx = resources.getDimension(R.dimen.default_rounded_corner_of_track_image_wide)
        Glide.with(imgTrackIcon)
            .load(currentTrack.coverArtWorkUrl)
            .placeholder(R.drawable.img_track_placeholder_wide)
            .error(R.drawable.img_track_placeholder_wide)
            .transform(CenterCrop(), RoundedCorners(roundedCornerValueInPx.toInt()))
            .into(imgTrackIcon)
    }

    private fun initTrackInformation() {

        textViewTrackName = findViewById(R.id.textViewTrackName)
        textViewTrackName.text = currentTrack.trackName

        textViewGroupName = findViewById(R.id.textViewGroupName)
        textViewGroupName.text = currentTrack.artistName

        textViewTrackDurationValue = findViewById(R.id.textViewTrackDurationValue)
        textViewTrackDurationValue.text = currentTrack.trackTime

        textViewTrackAlbumValue = findViewById(R.id.textViewTrackAlbumValue)
        textViewTrackAlbumValue.text = currentTrack.collectionName

        textViewTrackYearValue = findViewById(R.id.textViewTrackYearValue)
        textViewTrackYearValue.text = currentTrack.year

        textViewTrackGenreValue = findViewById(R.id.textViewTrackGenreValue)
        textViewTrackGenreValue.text = currentTrack.primaryGenreName

        textViewTrackCountryValue = findViewById(R.id.textViewTrackCountryValue)
        textViewTrackCountryValue.text = currentTrack.country
    }

    private fun initButtonReturn() {
        buttonReturn = findViewById(R.id.buttonReturn)
        buttonReturn.setOnClickListener {
            this@MusicPlayActivity.finish()
        }
    }

    companion object{
        val JSON_FORMAT_TRACK_KEY : String = "trackInJsonFormat"
    }
}