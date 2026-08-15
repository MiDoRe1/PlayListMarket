package com.example.playlistmarket.settings.data

import android.content.Context

class ResourceAndroidClient(private val context: Context) {
    fun getResourceValue(name: String): String {
        val id = context.resources.getIdentifier(
            name,
            "string",
            context.packageName
        )
        return if (id != 0) {
            context.getString(id)
        } else {
            ""
        }
    }
}