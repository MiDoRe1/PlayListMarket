package com.example.playlistmarket

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.text.format.Formatter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DatePickerFormatter
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Runnable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.os.Handler
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration


class Test : AppCompatActivity() {

    var timerThread: Thread? = null
    private val timer = Timer(
        2000,
        Handler(Looper.getMainLooper())
    ) {
        txtViewCurrentTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }
    private lateinit var buttonPlayer : Button
    private lateinit var txtViewCurrentTime : TextView
    var url = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/mzaf_4002676889906514534.plus.aac.p.m4a"
    private val mediaPlayer = MediaPlayer()
    private var statusOfPlayer : CurrentStatusOfPlayer? = null

    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        statusOfPlayer = CurrentStatusOfPlayer.READY_TO_PLAY
        buttonPlayer.isEnabled = true
    }

    private val onCompletionListener = MediaPlayer.OnCompletionListener {
            timerThread?.interrupt()
            statusOfPlayer = CurrentStatusOfPlayer.READY_TO_PLAY
            buttonPlayer.text = "Play"
            txtViewCurrentTime.text = "00:00"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buttonPlayer = findViewById<Button>(R.id.buttonPlayer)
        buttonPlayer.isEnabled = false
        buttonPlayer.setOnClickListener {
            when (statusOfPlayer) {
                CurrentStatusOfPlayer.READY_TO_PLAY, CurrentStatusOfPlayer.PAUSE  -> doPlayLogic()
                CurrentStatusOfPlayer.PLAY -> doPauseLogic()
                else -> {}
            }
        }
        txtViewCurrentTime = findViewById<TextView>(R.id.textViewCurrentTime)

        statusOfPlayer = CurrentStatusOfPlayer.SET_DATA_SOURCE_NEEDED
        initMediaPlayer()


        val myHandler = android.os.Handler(Looper.getMainLooper())




    }

    private fun doPlayLogic() {
        buttonPlayer.text = "Pause"
        statusOfPlayer = CurrentStatusOfPlayer.PLAY
        mediaPlayer.start()
        timerThread = Thread(timer)
        timerThread?.start()
    }

    private fun doPauseLogic() {
        buttonPlayer.text = "Play"
        statusOfPlayer = CurrentStatusOfPlayer.PAUSE
        mediaPlayer.pause()
        timerThread?.interrupt()
    }

    private fun initMediaPlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener(onPreparedListener)
        mediaPlayer.setOnCompletionListener(onCompletionListener)
        mediaPlayer.prepareAsync()
        statusOfPlayer = CurrentStatusOfPlayer.DATA_SOURCE_SET
    }

    companion object{

    }

    private enum class CurrentStatusOfPlayer{
        SET_DATA_SOURCE_NEEDED,
        DATA_SOURCE_SET,
        PREPARED,
        READY_TO_PLAY,
        PLAY,
        PAUSE,
        STOPPED
    }
}

class Timer(
    val tickTimePeriodInMilliseconds: Int,
    val handlerTick: Handler,
    val callBack: OnTimeTickListener
): Runnable {
    override fun run() {
        while (!Thread.currentThread().isInterrupted) {
            handlerTick.post {
                callBack.doOnTick()
            }
            try {
            Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                return
            }
        }
    }

    fun interface OnTimeTickListener{
        fun doOnTick()
    }
}
