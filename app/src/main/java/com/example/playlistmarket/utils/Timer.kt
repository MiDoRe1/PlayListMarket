package com.example.playlistmarket.utils

import android.os.Handler

class Timer(
    val tickTimePeriodInMilliseconds: Long,
    val handlerTick: Handler,
    val callBack: OnTimeTickListener
): Runnable {
    override fun run() {
        callBack.doOnTick()
        handlerTick.postDelayed(this, tickTimePeriodInMilliseconds)
    }

    fun start() {
        stop()
        handlerTick.post(this)
    }

    fun stop() {
        handlerTick.removeCallbacks(this)
    }

    fun interface OnTimeTickListener{
        fun doOnTick()
    }
}