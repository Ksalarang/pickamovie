package com.diyartaikenov.pickamovie.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.TAG
import com.diyartaikenov.pickamovie.databinding.FragmentSeriesBinding

class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close the app on back button pressed
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                requireActivity().finish()
            }

        binding.btToBlank.setOnClickListener {
            findNavController().navigate(R.id.action_nav_series_to_nav_blank_fragment)
        }
    }
}