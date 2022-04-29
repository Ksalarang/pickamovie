package com.diyartaikenov.pickamovie.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diyartaikenov.pickamovie.databinding.FragmentHomeBinding
import com.diyartaikenov.pickamovie.ui.adapter.MovieListAdapter
import com.diyartaikenov.pickamovie.viewmodel.MovieViewModel

const val TAG = "myTag"

class HomeFragment : Fragment() {

    private lateinit var viewModel: MovieViewModel

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = MovieViewModel(requireContext())
        val adapter = MovieListAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            adapter.submitList(movies)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            Log.d(TAG, "onViewCreated: $error")
        }
    }
}