package com.example.playlistmarket

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
import kotlin.time.Duration


class Test : AppCompatActivity() {

    var timerThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val bt = findViewById<Button>(R.id.buttonStart)
        val txtViewCurrentTime = findViewById<TextView>(R.id.textViewCurrentTime)
        val editTextTotalTime = findViewById<EditText>(R.id.editTextTotalTime)

        val myHandler = android.os.Handler(Looper.getMainLooper())



        bt.setOnClickListener { v ->

            timerThread?.interrupt()

            if (timerThread == null || !timerThread!!.isAlive) {
                timerThread = Thread(
                    TimeWorker(
                        editTextTotalTime.text.toString().toInt(),
                        myHandler,
                        object : TimeWorker.OnTimeWorkerListener {
                            override fun doOnTick(formatedTime: String) {
                                txtViewCurrentTime.text = formatedTime
                            }

                            override fun doOnFinish() {
                                txtViewCurrentTime.text = "Done!"
                            }
                        }
                    )
                )
                timerThread!!.start()
            }

        }
    }

    companion object{

    }
}

class TimeWorker(
    totalTimeInSeconds: Int,
    val handlerTick: Handler,
    val callBack: OnTimeWorkerListener
): Runnable {
    var currentTime: LocalTime = LocalTime.of(0,0,0)
    var totalTime = LocalTime.ofSecondOfDay(totalTimeInSeconds.toLong())

    fun getCurrentTimeInString(): String {
        return currentTime.format(DateTimeFormatter.ofPattern("mm:ss"))
    }

    fun addOneSecond() {
        currentTime = currentTime.plusSeconds(1)
    }

    override fun run() {
        while (totalTime != currentTime && !Thread.currentThread().isInterrupted) {
            val postTimeInFormat = getCurrentTimeInString()
            handlerTick.post {
                callBack.doOnTick(postTimeInFormat)
            }
            try {
            Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                return
            }
            addOneSecond()
        }
        if (!Thread.currentThread().isInterrupted) {
            handlerTick.post {
                callBack.doOnFinish()
            }
        }
    }

    interface OnTimeWorkerListener{
        fun doOnTick(formatedTime: String)
        fun doOnFinish()
    }


}