package ru.netology.nerecipe.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.adapters.filters.FiltersAdapter
import ru.netology.nerecipe.databinding.FragmentFiltersBinding
import ru.netology.nerecipe.dto.Categories
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FiltersFragment : Fragment() {

    private val Fragment.packageManager
        get() = activity?.packageManager

    private val viewModel by activityViewModels<RecipeViewModel>()

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFiltersBinding.inflate(layoutInflater, container, false)
            .also { binding ->

                val sharedPref =
                    activity?.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

                viewModel.listOfFilters.observe(viewLifecycleOwner) { listOfFilters ->
                    binding.filtersAllCategoryCheckBox.isChecked = listOfFilters.isEmpty()
                    binding.filtersAllCategoryCheckBox.isClickable = false
                }

                val listOfCategoryNames = mutableListOf<String>()
                for (i in 0 until Categories.values().size) {
                    listOfCategoryNames.add(Categories.values()[i].categoryName)
                }

                val filtersAdapter = FiltersAdapter(viewModel, sharedPref, viewLifecycleOwner)
                binding.filtersRecyclerView.adapter = filtersAdapter

                viewModel.data.observe(viewLifecycleOwner) {
                    filtersAdapter.submitList(listOfCategoryNames)
                }

                binding.applyFilterButton.setOnClickListener {
                    viewModel.onApplyFiltersButtonClicked()
                    findNavController().popBackStack()
                }

                binding.resetFilterButton.setOnClickListener {
                    if (sharedPref != null) {
                        with(sharedPref.edit()) {
                            for (i in 0 until Categories.values().size) {
                                putBoolean(i.toString(), false)


                            }
                            apply()
                        }
                    }
                    viewModel.onResetFiltersButtonClicked()
                    viewModel.filterCheckboxUpdate.value = true
                }

                onBackPressedCallback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        binding.resetFilterButton.performClick()
                        binding.applyFilterButton.performClick()
                    }
                }
                requireActivity().onBackPressedDispatcher.addCallback(
                    onBackPressedCallback
                )

            }.root


    }
    class FilterFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ) = FragmentFiltersBinding.inflate(layoutInflater).also { binding ->

            binding.okFilterButton.setOnClickListener {
                onOkFilterButtonClicked(binding)
                //findNavController().navigateUp()
            }
        }.root

        companion object {
            const val SHARED_PREFS_KEY = "ru.netology.nerecipe.PREFERENCE"
        }

        private fun onOkFilterButtonClicked(binding: FragmentFiltersBinding) {

            var numberOfCheckedCategories = 0

            val listOfCategoryNames : ArrayList<String> = ArrayList()

            if (binding.category_European.isChecked) {
                listOfCategoryNames.add(binding.category_European.text.toString())
                numberOfCheckedCategories++
            }

            if (binding.category_Asian.isChecked) {
                listOfCategoryNames.add(binding.category_Asian.text.toString())
                numberOfCheckedCategories++
            }

            if (binding.category_PanAsian.isChecked) {
                listOfCategoryNames.add(binding.category_PanAsian.text.toString())
                numberOfCheckedCategories++
            }

            if (binding.category_Oriental.isChecked) {
                listOfCategoryNames.add(binding.category_Oriental.text.toString())
                numberOfCheckedCategories++
            }

            if (binding.category_American.isChecked) {
                listOfCategoryNames.add(binding.category_American.text.toString())
                numberOfCheckedCategories++
            }

            if (binding.category_Russian.isChecked) {
                listOfCategoryNames.add(binding.category_Russian.text.toString())
                numberOfCheckedCategories++
            }

            if (binding.category_Mediterranean.isChecked) {
                listOfCategoryNames.add(binding.category_Mediterranean.text.toString())
                numberOfCheckedCategories++
            }

            if (numberOfCheckedCategories == 0) {
                Toast.makeText(requireContext(), "Please choose at least one filter", Toast.LENGTH_LONG)
                    .show()

            }
            val resultBundle = Bundle(1)
            resultBundle.putStringArrayList("listOfChosenCategories", listOfCategoryNames)
            setFragmentResult("chosenCategories", resultBundle)
            findNavController().popBackStack()
        }

    }
}






