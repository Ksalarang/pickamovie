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
import android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE
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
/**
 * This fragment offers sorting and filtering options for getting movies from network.
 */
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
    private val defaultQueryParams = QueryParams()

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
            val genresResult = moviesViewModel.getGenres()
            if (genresResult.isSuccess) {
                genresResult.getOrNull()!!.collectLatest { genres ->
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
                Log.d("myTagMovieFilters", "onCreateView: " +
                        "${genresResult.exceptionOrNull()!!.message}")
                // TODO: handle data query error
            }
        }
        lifecycleScope.launch {
            val certsResult = moviesViewModel.getCertifications()
            if (certsResult.isSuccess) {
                certsResult.getOrNull()!!.collectLatest { certs ->
                    val list = certs.map { it.value }
                    binding.minCertificationSpinner.adapter = createSpinnerAdapterFromList(list)
                    binding.maxCertificationSpinner.adapter = createSpinnerAdapterFromList(list)
                }
            } else {
                Log.d("myTagMovieFilters", "onCreateView: " +
                        "${certsResult.exceptionOrNull()!!.message}")
            }
        }
        // Initialize all widgets
        binding.apply {
            sortSpinner.adapter = createSpinnerAdapterFromResource(R.array.sort_options)
            showVotePickersSwitch.setOnCheckedChangeListener { _, isChecked ->
                voteAverageRangeLabel.text = if (isChecked) {
                    minVotePicker.visibility = View.VISIBLE
                    maxVotePicker.visibility = View.VISIBLE
                    labelBetweenVotePickers.visibility = View.VISIBLE
                    getString(R.string.vote_average, getString(R.string.range))
                } else {
                    minVotePicker.visibility = View.GONE
                    maxVotePicker.visibility = View.GONE
                    labelBetweenVotePickers.visibility = View.GONE
                    getString(R.string.vote_average, getString(R.string.any))
                }
            }
            minVotePicker.apply {
                minValue = defaultQueryParams.minVoteAverage.toInt()
                maxValue = defaultQueryParams.maxVoteAverage.toInt()
                setOnScrollListener { _, scrollState ->
                    if (scrollState == SCROLL_STATE_IDLE && value > maxVotePicker.value) {
                        value = maxVotePicker.value
                    }
                }
            }
            maxVotePicker.apply {
                minValue = defaultQueryParams.minVoteAverage.toInt()
                maxValue = defaultQueryParams.maxVoteAverage.toInt()
                setOnScrollListener { _, scrollState ->
                    if (scrollState == SCROLL_STATE_IDLE && value < minVotePicker.value) {
                        value = minVotePicker.value
                    }
                }
            }
            voteCountSeekbar.max = voteCountRange.size - 1
            voteCountSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    voteCountSeekbarLabel.text = if (switchMaxMinVoteCount.isChecked) {
                        getString(R.string.max_vote_count_label, voteCountRange[progress])
                    } else {
                        getString(R.string.min_vote_count_label, voteCountRange[progress])
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            switchMaxMinVoteCount.setOnCheckedChangeListener { _, isChecked ->
                val voteCount = voteCountRange[voteCountSeekbar.progress]
                voteCountSeekbarLabel.text = if (isChecked) {
                    getString(R.string.max_vote_count_label, voteCount)
                } else {
                    getString(R.string.min_vote_count_label, voteCount)
                }
            }
            showCertificateSpinnersSwitch.setOnCheckedChangeListener { _, isChecked ->
                certificationLabel.text = if (isChecked) {
                    minCertificationSpinner.visibility = View.VISIBLE
                    maxCertificationSpinner.visibility = View.VISIBLE
                    getString(R.string.certification_label, getString(R.string.range))
                } else {
                    minCertificationSpinner.visibility = View.GONE
                    maxCertificationSpinner.visibility = View.GONE
                    getString(R.string.certification_label, getString(R.string.any))
                }
            }
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

    /**
     * Create a genre [TextView] and fill it with data from [genre].
     */
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

    /**
     * Update all filter views by the given [queryParams].
     */
    private fun updateFilters(queryParams: QueryParams) {
        binding.apply {
            sortSpinner.setSelection(queryParams.sortBy.ordinal)
            genreViews.forEach { genreView ->
                val isSelected = queryParams.withGenres.contains(genreView.tag as Int)
                setGenreViewSelection(genreView, isSelected)
            }
            isGenresExclusiveSwitch.isChecked = queryParams.withoutGenres.isNotEmpty()

            showVotePickersSwitch.isChecked =
                queryParams.minVoteAverage != defaultQueryParams.minVoteAverage
                        || queryParams.maxVoteAverage != defaultQueryParams.maxVoteAverage

            voteAverageRangeLabel.text = if (showVotePickersSwitch.isChecked) {
                getString(R.string.vote_average, getString(R.string.range))
            } else {
                getString(R.string.vote_average, getString(R.string.any))
            }
            minVotePicker.value = queryParams.minVoteAverage.toInt()
            maxVotePicker.value = queryParams.maxVoteAverage.toInt()

            // If maxVoteCount differs from Int.MAX_VALUE then it's being used.
            // So the vote count switch should be checked, and unchecked otherwise.
            switchMaxMinVoteCount.isChecked = queryParams.maxVoteCount != Int.MAX_VALUE
            val progress: Int
            voteCountSeekbarLabel.text = if (switchMaxMinVoteCount.isChecked) {
                progress = voteCountRange.indexOf(queryParams.maxVoteCount)
                getString(R.string.max_vote_count_label, queryParams.maxVoteCount)
            } else {
                progress = voteCountRange.indexOf(queryParams.minVoteCount)
                getString(R.string.min_vote_count_label, queryParams.minVoteCount)
            }
            voteCountSeekbar.progress = progress
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
        val withGenresIds = genreViews.filter { it.isSelected }.map { it.tag as Int }
        val withoutGenresIds: List<Int> = if (binding.isGenresExclusiveSwitch.isChecked) {
            genreViews.filter { !it.isSelected }.map { it.tag as Int }
        } else { listOf() }
        val selectedVoteCount = voteCountRange[binding.voteCountSeekbar.progress]
        val minVoteCount: Int
        val maxVoteCount: Int
        if (binding.switchMaxMinVoteCount.isChecked) {
            // Filter movies with vote count that is less than or equal to the selected value
            minVoteCount = 0
            maxVoteCount = selectedVoteCount
        } else {
            // Filter movies with vote count that is greater than or equal to the selected value
            minVoteCount = selectedVoteCount
            maxVoteCount = Int.MAX_VALUE
        }

        var minVoteAverage: Float
        val maxVoteAverage: Float
        if (binding.showVotePickersSwitch.isChecked) {
            minVoteAverage = binding.minVotePicker.value.toFloat()
            maxVoteAverage = binding.maxVotePicker.value.toFloat()
            if (minVoteAverage > maxVoteAverage) {
                minVoteAverage = maxVoteAverage
            }
        } else {
            minVoteAverage = defaultQueryParams.minVoteAverage
            maxVoteAverage = defaultQueryParams.maxVoteAverage
        }

        moviesViewModel.getMoviesWithQueryParams(
            sortBy = sortBy,
            withGenres = withGenresIds,
            withoutGenres = withoutGenresIds,
            minVoteCount = minVoteCount,
            maxVoteCount = maxVoteCount,
            minVoteAverage = minVoteAverage,
            maxVoteAverage = maxVoteAverage,
        )
        (requireActivity() as MainActivity)
            .supportActionBar?.title = getString(R.string.app_name)
        dismiss()
    }

    /**
     * Reset filters by applying the [QueryParams] default options.
     */
    private fun onResettingFilters() {
        updateFilters(defaultQueryParams)
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

    private fun createSpinnerAdapterFromResource(textArrayResId: Int): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            requireContext(),
            textArrayResId,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun createSpinnerAdapterFromList(
        items: List<String>
    ): ArrayAdapter<String> {
        return ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items,
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}