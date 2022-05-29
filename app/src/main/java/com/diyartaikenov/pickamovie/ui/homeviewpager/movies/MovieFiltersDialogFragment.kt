package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.allViews
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.DialogFragmentMovieFiltersBinding
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import com.google.android.flexbox.FlexboxLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFiltersDialogFragment(
    private val moviesViewModel: MoviesViewModel
) : DialogFragment() {

    private var _binding: DialogFragmentMovieFiltersBinding? = null
    private val binding get() = _binding!!

    private val genreViewLayoutParams = FlexboxLayout.LayoutParams(
        FlexboxLayout.LayoutParams.WRAP_CONTENT,
        FlexboxLayout.LayoutParams.WRAP_CONTENT
    )

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFragmentMovieFiltersBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        genreViewLayoutParams
            .setMargins(resources.getDimensionPixelSize(R.dimen.filter_genres_margin))

        moviesViewModel.genres.observe(viewLifecycleOwner) { genres ->
            // Create a TextView for each genre and display it in this Dialog
            genres.forEach { genre ->
                val genreView = TextView(context).apply {
                    setupAttributes(this)
                    text = genre.name
                    tag = genre.id
                    // Set this genre textView selected by performing
                    // a click on it, if it was selected before.
                    if (moviesViewModel.queryParams.withGenres.contains(genre.id)) {
                        onGenreViewClick(this)
                    }
                    setOnClickListener {
                        onGenreViewClick(this)
                    }
                }
                binding.flexboxLayout.post {
                    binding.flexboxLayout.addView(genreView)
                }
            }
        }
        binding.buttonApplyFilters.setOnClickListener {
            onApplyingFilters()
        }

        return binding.root
    }

    /**
     * Get movie data from network according to the applied filters.
     */
    private fun onApplyingFilters() {
        val genresIds = mutableListOf<Int>()
        binding.flexboxLayout.allViews.filter { it.isSelected }.forEach {
            genresIds.add(it.tag as Int)
        }
        moviesViewModel.getMoviesWithQueryParams(
            withGenres = genresIds,
        )
        dismiss()
    }

    /**
     * Change the [textView]'s selection state and its background color accordingly.
     */
    private fun onGenreViewClick(textView: TextView) {
        textView.apply {
            isSelected = !isSelected
            background = if (isSelected) {
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.shape_rounded_corners_colored,
                    context.theme
                )
            } else {
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.shape_rounded_corners,
                    context.theme
                )
            }
        }
    }

    /**
     * Set up attributes common for all genre textViews.
     */
    private fun setupAttributes(textView: TextView) {
        textView.apply {
            layoutParams = genreViewLayoutParams
            setPadding(resources.getDimensionPixelSize(R.dimen.filter_genres_padding))
            background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_rounded_corners,
                context.theme
            )
            isClickable = true
            isFocusable = true
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.filter_genres_text_size)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}