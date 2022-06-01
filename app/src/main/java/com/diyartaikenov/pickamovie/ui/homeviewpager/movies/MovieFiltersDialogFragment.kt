package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.DialogFragmentMovieFiltersBinding
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.network.QueryParams
import com.diyartaikenov.pickamovie.repository.network.SortBy
import com.diyartaikenov.pickamovie.ui.MainActivity
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import com.google.android.flexbox.FlexboxLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
    /**
     * A range of vote count numbers to filter movies with.
     */
    private val voteCountRange = arrayListOf(0, 50, 100, 500, 1000, 5000, 10000)

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

        lifecycleScope.launch {
            val result = moviesViewModel.getGenres()
            if (result.isSuccess) {
                result.getOrNull()!!.collectLatest { genres ->
                    genres.forEach { genre ->
                        val genreView = createGenreTextView(genre)
                        genreViews.add(genreView)
                        binding.flexboxLayout.post {
                            binding.flexboxLayout.addView(genreView)
                        }
                    }
                    updateFilters(moviesViewModel.queryParams)
                }
            } else {
                Log.d("myTag", "onCreateView: error: " +
                        "${result.exceptionOrNull()!!.message}")
                // TODO: handle data query error
            }
        }

        binding.apply {
            sortSpinner.adapter = createSpinnerAdapter()

            voteCountSeekbar.max = voteCountRange.size - 1
            voteCountSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    voteCountSeekbarLabel.text =
                        getString(R.string.vote_count_label, voteCountRange[progress])
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            buttonApplyFilters.setOnClickListener { onApplyingFilters() }
            buttonResetFilters.setOnClickListener { onResettingFilters() }
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        genreViews.clear()
        super.onDestroyView()
    }

    private fun createGenreTextView(genre: Genre): TextView {
        return TextView(context).apply {
            setupGenreViewAttributes(this)
            text = genre.name
            // Genre id is stored in the textView tag
            tag = genre.id
            setOnClickListener {
                setGenreViewSelection(this, !this.isSelected)
            }
        }
    }

    private fun updateFilters(queryParams: QueryParams) {
        binding.apply {
            sortSpinner.setSelection(queryParams.sortBy.ordinal)
            genreViews.forEach { genreView ->
                val isSelected = queryParams.withGenres.contains(genreView.tag as Int)
                setGenreViewSelection(genreView, isSelected)
            }
            voteCountSeekbarLabel.text =
                getString(R.string.vote_count_label, queryParams.minimalVoteCount)
            voteCountSeekbar.progress = voteCountRange.indexOf(queryParams.minimalVoteCount)
        }
    }

    /**
     * Set this view's selection state and its background.
     */
    private fun setGenreViewSelection(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
        view.background = if (isSelected) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_rounded_corners_selected,
                requireContext().theme
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_rounded_corners_unselected,
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
        val voteCount = voteCountRange[binding.voteCountSeekbar.progress]
        genreViews.filter { it.isSelected }.forEach {
            // movie id is stored in textView's tag
            genresIds.add(it.tag as Int)
        }
        moviesViewModel.getMoviesWithQueryParams(
            sortBy = sortBy,
            withGenres = genresIds,
            minimalVoteCount = voteCount,
        )
        (requireActivity() as MainActivity)
            .supportActionBar?.title = getString(R.string.app_name)
        dismiss()
    }

    /**
     * Reset filters by applying the [QueryParams] default options.
     */
    private fun onResettingFilters() {
        val queryParams = QueryParams()
        updateFilters(queryParams)
    }

    /**
     * Set up attributes common for all genre textViews.
     */
    private fun setupGenreViewAttributes(textView: TextView) {
        textView.apply {
            layoutParams = genreViewLayoutParams
            setPadding(resources.getDimensionPixelSize(R.dimen.filter_genres_padding))
            background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_rounded_corners_unselected,
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

    private fun createSpinnerAdapter(): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}