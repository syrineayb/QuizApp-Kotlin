package com.example.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quiz.databinding.FragmentRegionChoiceBinding

class RegionChoiceFragment : Fragment() {

    private var _binding: FragmentRegionChoiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegionChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCheckBoxes()

        binding.buttonFirst.setOnClickListener {
            val selectedRegions = getSelectedRegions()

            if (selectedRegions.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putStringArrayList("selectedRegions", selectedRegions)
                }

                findNavController().navigate(R.id.action_RegionsFragment_to_NumChoicesFragment, bundle)
            } else {
                showToast("Please select at least one region.")
            }
        }
    }

    private fun setupCheckBoxes() {
        val regionsArray = resources.getStringArray(R.array.regions_list_configuration)
        val checkBoxGroup = binding.checkBoxGroup

        for (region in regionsArray) {
            val checkBox = CheckBox(requireContext())
            checkBox.text = region
            checkBoxGroup.addView(checkBox)
        }
    }

    private fun getSelectedRegions(): ArrayList<String> {
        val selectedRegions = ArrayList<String>()
        val checkBoxGroup = binding.checkBoxGroup

        for (i in 0 until checkBoxGroup.childCount) {
            val checkBox = checkBoxGroup.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                selectedRegions.add(checkBox.text.toString())
            }
        }

        return selectedRegions
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
