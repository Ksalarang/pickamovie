package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.DialogFragmentMovieFiltersBinding
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.network.SortBy
import com.diyartaikenov.pickamovie.ui.MainActivity
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import com.google.android.flexbox.FlexboxLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFiltersDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentMovieFiltersBinding? = null
    private val binding get() = _binding!!

    private val moviesViewModel: MoviesViewModel by activityViewModels()

    private val genreViewLayoutParams = FlexboxLayout.LayoutParams(
        FlexboxLayout.LayoutParams.WRAP_CONTENT,
        FlexboxLayout.LayoutParams.WRAP_CONTENT
    )

    /**
     * A list that contains all [Genre] views. It's filled when the genre list
     * is received from the [moviesViewModel].
     */
    private val genreViews: MutableList<TextView> = mutableListOf()

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
                    genreViews.add(this)
                    setupAttributes(this)
                    text = genre.name
                    // Using a textView's tag to store the movie id.
                    tag = genre.id
                    if (moviesViewModel.queryParams.withGenres.contains(genre.id)) {
                        // Mark this genre view as selected if it was selected by the user before.
                        setGenreViewSelection(this, true)
                    } else {
                        // Unselect the genre view otherwise.
                        setGenreViewSelection(this, false)
                    }
                    setOnClickListener {
                        // Toggle selection state of this genre view
                        setGenreViewSelection(this, !this.isSelected)
                    }
                }
                binding.flexboxLayout.post {
                    binding.flexboxLayout.addView(genreView)
                }
            }
        }
        binding.apply {
            buttonApplyFilters.setOnClickListener {
                onApplyingFilters()
            }
            buttonResetFilters.setOnClickListener {
                onResettingFilters()
            }

            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sortSpinner.adapter = adapter
            }
            sortSpinner.setSelection(moviesViewModel.queryParams.sortBy.ordinal)
        }

        return binding.root
    }

    /**
     * Set this view's selection state and its background.
     */
    private fun setGenreViewSelection(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
        view.background = if (isSelected) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_rounded_corners_colored,
                requireContext().theme
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_rounded_corners,
                requireContext().theme
            )
        }
    }

    /**
     * Get movie data from network according to the applied filters.
     */
    private fun onApplyingFilters() {
        val sortBy = SortBy.values()[binding.sortSpinner.selectedItemPosition]
        val genresIds = mutableListOf<Int>()
        genreViews.filter { it.isSelected }.forEach {
            // movie id is stored in textView's tag
            genresIds.add(it.tag as Int)
        }
        moviesViewModel.getMoviesWithQueryParams(
            sortBy = sortBy,
            withGenres = genresIds,
        )
        (requireActivity() as MainActivity)
            .supportActionBar?.title = getString(R.string.app_name)
        dismiss()
    }

    /**
     * Reset all filters to default.
     */
    private fun onResettingFilters() {
        binding.apply {
            sortSpinner.setSelection(0)
            genreViews.filter { it.isSelected }.forEach {
                setGenreViewSelection(it, false)
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
        _binding = null
        genreViews.clear()
        super.onDestroyView()
    }
}