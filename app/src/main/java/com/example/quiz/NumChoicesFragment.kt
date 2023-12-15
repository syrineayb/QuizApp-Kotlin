package com.example.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quiz.databinding.FragmentNumChoicesBinding

class NumChoicesFragment : Fragment() {

    private var _binding: FragmentNumChoicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNumChoicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumChoicesRadioGroup()

        binding.buttonNumChoicesNext.setOnClickListener {
            val selectedNumResponses = getSelectedNumResponses()

            if (selectedNumResponses > 0) {
                val bundle = Bundle().apply {
                    putInt("selectedNumResponses", selectedNumResponses)
                }

                findNavController().navigate(R.id.action_NumChoicesFragment_to_FlagsQuiz, bundle)
            } else {
                showToast("Please select a number of choices.")
            }
        }
    }

    private fun setupNumChoicesRadioGroup() {
        val choicesArray = resources.getStringArray(R.array.choix_list)
        val radioGroup = binding.radioGroupNumResponses

        for (choice in choicesArray) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = choice
            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.layoutParams = params

            radioGroup.addView(radioButton)
        }
    }

    private fun getSelectedNumResponses(): Int {
        val selectedRadioButtonId = binding.radioGroupNumResponses.checkedRadioButtonId
        val selectedRadioButton = binding.radioGroupNumResponses.findViewById<RadioButton>(selectedRadioButtonId)
        return selectedRadioButton?.text.toString().toIntOrNull() ?: 0
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
