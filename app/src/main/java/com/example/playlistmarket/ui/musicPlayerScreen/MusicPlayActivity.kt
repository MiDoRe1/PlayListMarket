package com.example.playlistmarket.ui.musicPlayerScreen

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.R
import com.example.playlistmarket.domain.models.Track
import com.example.playlistmarket.utils.MediaPlayerWorker
import com.example.playlistmarket.utils.Timer
import com.example.playlistmarket.utils.convertMillisecondsInNeededStringFormat
import com.google.gson.Gson

class MusicPlayActivity : AppCompatActivity() {

    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var  timer : Timer

    private lateinit var mediaPlayerWorker : MediaPlayerWorker

    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        buttonPlay.isEnabled = true
        showPlayButton()
    }
    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        showPlayButton()
    }
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

    private lateinit var buttonPlay : ImageView

    private lateinit var textViewCurrentTimeOfTrack : TextView

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
        initTextViewCurrentTimeOfTrack()
        initButtonPlay()
        initMediaPlayerWorker()
        initTimer()
    }

    private fun initTimer() {
        timer = Timer(
            TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER,
            mainHandler
        ) {
            textViewCurrentTimeOfTrack.text = convertMillisecondsInNeededStringFormat(
                mediaPlayerWorker.currentPosition.toLong()
            )
        }
    }

    private fun initMediaPlayerWorker() {
        try {
            mediaPlayerWorker = MediaPlayerWorker(
                onPreparedListener,
                onCompletionListener
            )
            mediaPlayerWorker.setDataSource(currentTrack.previewUrl)
            mediaPlayerWorker.prepareAsync()
        } catch (e: Exception) {
            Toast.makeText(this,
                "Empty url from API : ${currentTrack.previewUrl}",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun initButtonPlay() {
        buttonPlay = findViewById(R.id.buttonPlay)
        buttonPlay.isEnabled = false
        buttonPlay.setOnClickListener {
            when (mediaPlayerWorker.status) {
                MediaPlayerWorker.CurrentStatusOfPlayer.PREPARED,
                MediaPlayerWorker.CurrentStatusOfPlayer.PAUSE -> {
                    doPlayLogic()
                }

                MediaPlayerWorker.CurrentStatusOfPlayer.PLAY -> {
                    doPauseLogic()
                }
                else -> {}
            }
        }
    }


    private fun doPlayLogic() {
        showPauseButton()
        mediaPlayerWorker.play()
        timer.start()
    }

    private fun doPauseLogic() {
        showPlayButton()
        mediaPlayerWorker.pause()
        timer.stop()
    }

    private fun showPauseButton() {
        buttonPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun showPlayButton() {
        buttonPlay.setImageResource(R.drawable.ic_play)
    }
    private fun initTextViewCurrentTimeOfTrack() {
        textViewCurrentTimeOfTrack = findViewById(R.id.textViewCurrentTimeOfTrack)
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

    override fun onPause() {
        super.onPause()
        doPauseLogic()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.stop()
        mediaPlayerWorker.resume()
    }

    companion object{
        val JSON_FORMAT_TRACK_KEY : String = "trackInJsonFormat"
        val TIME_IN_MILLISECOND_TO_UPDATE_PLAY_TIMER = 1000L

    }
}