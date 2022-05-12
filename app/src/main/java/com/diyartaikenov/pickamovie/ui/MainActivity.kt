package com.diyartaikenov.pickamovie.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "myTag"

/**
 * A Main activity that hosts all Fragments for this app and hosts the nav controller.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.bottomNavView.setupWithNavController(
            findNavController(R.id.nav_host_fragment)
        )
    }
}