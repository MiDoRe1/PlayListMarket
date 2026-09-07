package com.example.playlistmarket.main.ui.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmarket.R
import com.example.playlistmarket.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity(R.layout.activity_root) {

    lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.bind(findViewById<ViewGroup>(android.R.id.content).getChildAt(0))
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_main) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationViewMain.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.musicPlayerFragment -> hideBottomNavUI()
                else -> showBottomNavUI()
            }
        }
    }

    private fun hideBottomNavUI() {
        binding.dividingLine.visibility = View.GONE
        binding.bottomNavigationViewMain.visibility = View.GONE
    }

    private fun showBottomNavUI() {
        binding.dividingLine.visibility = View.VISIBLE
        binding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}