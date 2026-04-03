package com.example.playlistmarket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button_seacrh: MaterialButton = findViewById<MaterialButton>(R.id.button_go_to_search)
        button_seacrh.setOnClickListener {
            Toast.makeText(this@MainActivity, "push \"go to search\"", Toast.LENGTH_LONG)
                .show()
        }

        val button_library: MaterialButton = findViewById<MaterialButton>(R.id.button_go_to_library)
        button_library.setOnClickListener {
            Toast.makeText(this@MainActivity, "push \"go to library\"", Toast.LENGTH_LONG)
                .show()
        }

        val button_settings: MaterialButton = findViewById<MaterialButton>(R.id.button_go_to_settings)
        button_settings.setOnClickListener {
            Toast.makeText(this@MainActivity, "push \"go to settings\"", Toast.LENGTH_LONG)
                .show()
        }

        button_seacrh.setOnClickListener(
            object: View.OnClickListener{
                override fun onClick(v: View?) {
                    val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                    startActivity(searchIntent)
            }
        })

        button_library.setOnClickListener(
            object: View.OnClickListener{
                override fun onClick(v: View?) {
                    val libraryIntent = Intent(this@MainActivity, LibraryActivity::class.java)
                    startActivity(libraryIntent)
                }
            })

        button_settings.setOnClickListener(
            object: View.OnClickListener{
                override fun onClick(v: View?) {
                    val settingIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(settingIntent)
                }
            })





    }
}