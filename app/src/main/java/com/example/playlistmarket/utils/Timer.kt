package com.example.playlistmarket.utils

import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.Runnable

class Timer(
    val tickTimePeriodInMilliseconds: Long,
    val callBack: OnTimeTickListener
) {

    private val tick = object : Runnable {
        override fun run() {
            callBack.doOnTick()
            handler.postDelayed(this, tickTimePeriodInMilliseconds)
        }
    }

    private val thread = HandlerThread(this.toString()).apply {
        start()
    }

    private val handler = Handler(thread.looper)
    fun start() {
        stop()
        handler.postDelayed(tick, tickTimePeriodInMilliseconds)
    }

    fun stop() {
        handler.removeCallbacks(tick)
    }

    fun release() {
        stop()
        thread.quitSafely() // Освобождает ресурсы ОС и убивает поток
    }

    fun interface OnTimeTickListener{
        fun doOnTick()
    }
}