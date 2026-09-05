package com.example.playlistmarket.medialibrary.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.ActivityMedialibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MedialibraryActivity : AppCompatActivity(R.layout.activity_medialibrary) {

    private lateinit var binding: ActivityMedialibraryBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        binding = ActivityMedialibraryBinding.bind(rootView)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbarHeader.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.pager.adapter = MediaLibraryPagerAdapter(supportFragmentManager, lifecycle)

        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.pager) {
            tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}