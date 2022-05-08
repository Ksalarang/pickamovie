package com.diyartaikenov.pickamovie.ui.homeviewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.FragmentViewPagerHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerHomeBinding.inflate(inflater, container, false)

        val fragments = arrayListOf(
            MoviesFragment(),
            TvShowsFragment()
        )
        val viewPagerAdapter = ViewPagerAdapter(
            fragments,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_label_movies)
                1 -> getString(R.string.tab_label_tv_shows)
                else -> getString(R.string.tab_label_new)
            }
        }.attach()

        return binding.root
    }
}