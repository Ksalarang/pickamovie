package com.diyartaikenov.pickamovie.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
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

        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT in 21..29) {
            // Make this activity's window to be responsible
            // for drawing the status bar background
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility =
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        else if (Build.VERSION.SDK_INT >= 30) {
            // Making status bar overlaps with the activity
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    /**
     * Set the visibility state of [MainActivity]'s BottomNavigationView
     *
     * @param visibility One of [View.VISIBLE], [View.INVISIBLE] OR [View.GONE].
     */
    fun setBottomNavigationVisibility(visibility: Int) {
        binding.bottomNavView.visibility = visibility
    }
}