package com.diyartaikenov.pickamovie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.diyartaikenov.pickamovie.databinding.ActivityMainBinding

const val TAG = "myTag"

/**
 * A Main activity that hosts all Fragments for this app and hosts the nav controller.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.setupWithNavController(
            findNavController(R.id.nav_host_fragment)
        )
    }
}