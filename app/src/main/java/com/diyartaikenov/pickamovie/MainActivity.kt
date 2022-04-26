package com.diyartaikenov.pickamovie

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.*
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.diyartaikenov.pickamovie.databinding.ActivityMainBinding
import com.diyartaikenov.pickamovie.ui.MoviesFragment
import com.diyartaikenov.pickamovie.ui.SeriesFragment

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

        val navController = findNavController(R.id.nav_host_fragment)

        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_movies -> {
                    navController.navigate(
                        R.id.nav_movies,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.nav_movies, true)
                            .build()
                    )
                    true
                }
                R.id.nav_series -> {
                    navController.navigate(
                        R.id.nav_series,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.nav_series, true)
                            .build()
                    )
                    true
                }
                else -> {
                    false
                }
            }
        }
    }


}