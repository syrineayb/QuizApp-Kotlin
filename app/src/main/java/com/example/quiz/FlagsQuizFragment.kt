package com.example.quiz

import android.content.Intent
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.quiz.databinding.FragmentFlagsQuizBinding
import java.io.IOException
import java.io.InputStream

class FlagsQuizFragment : Fragment() {
    private var numberOfChoices: Int = 0
    private var numberOfRegions: Int = 0
    private var _binding: FragmentFlagsQuizBinding? = null
    private val binding get() = _binding!!
    private var correctCountryName: String = ""
    private var userScore: Int = 0
    private var currentQuestion: Int = 1
    private var totalQuestions: Int = 10
    private var selectedRegions: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFlagsQuizBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var selectedRegion: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numberOfChoices = arguments?.getInt("defaultNumChoices") ?: 0
        numberOfRegions = arguments?.getInt("defaultNumRegions") ?: 0
        selectedRegions = arguments?.getStringArrayList("selectedRegions")
        selectedRegion = arguments?.getString("selectedRegion") ?: ""

        setButtonListeners()
        startNewQuestion()
    }

    private fun setButtonListeners() {
        val buttonsLayout = binding.buttonsLinearLayout

        for (i in 1..numberOfChoices / 2) {
            val rowLayout = createRowLayout()
            val button1 = createButton()
            val button2 = createButton()

            rowLayout.addView(button1)
            rowLayout.addView(button2)
            buttonsLayout.addView(rowLayout)

            setButtonClickListeners(button1, button2)
        }
    }

    private fun createRowLayout(): LinearLayout {
        val rowLayout = LinearLayout(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        rowLayout.layoutParams = params
        rowLayout.orientation = LinearLayout.HORIZONTAL
        return rowLayout
    }

    private fun createButton(): Button {
        val button = Button(requireContext())
        val buttonParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1.0f
        )
        button.layoutParams = buttonParams
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_text_color))
        return button
    }

    private fun setButtonClickListeners(button1: Button, button2: Button) {
        button1.setOnClickListener { checkAnswer(button1.text.toString()) }
        button2.setOnClickListener { checkAnswer(button2.text.toString()) }
    }

    private fun startNewQuestion() {
        currentQuestion++
        if (currentQuestion <= totalQuestions) {
            displayRandomImageFromAssets()
            setButtonTextsRandomly()
            updateQuestionNumber()
        } else {
            showFinalScore()
            navigateToMainActivity()
        }
    }

    private fun checkAnswer(selectedCountry: String) {
        if (selectedCountry == correctCountryName) {
            userScore++
            showAnswerFeedback("Correct!", R.color.reponse_correcte)
        } else {
            showAnswerFeedback("Incorrect!", R.color.reponse_incorrecte)
            val shakeIncorrect = AnimationUtils.loadAnimation(requireContext(),R.anim.vibration_incorrect)
            binding.drapeauxImageView.startAnimation(shakeIncorrect)
        }
        startNewQuestion()
    }

    private fun showAnswerFeedback(message: String, colorResource: Int) {
        binding.reponseTextView.text = message
        val textColor = resources.getColor(colorResource, requireContext().theme)
        binding.reponseTextView.setTextColor(textColor)
    }

    private fun showFinalScore() {
        val message = when {
            userScore == totalQuestions -> "Congratulations! "
            userScore >= totalQuestions / 2 -> "Good job! "
            else -> "Keep practicing! "
        }

        val finalScoreMessage = "Final Score: $userScore/$totalQuestions\n$message"
        Toast.makeText(requireContext(), finalScoreMessage, Toast.LENGTH_LONG).show()
    }

    private fun displayRandomImageFromAssets() {
        try {
            val assetManager: AssetManager = requireContext().assets

            val randomRegion = selectedRegions?.random() ?: resources.getStringArray(R.array.regions_list).random()

            val files: Array<String>? = assetManager.list(randomRegion)
            val imageFiles = files?.filter { it.endsWith(".png") }
            val randomImageFileName = imageFiles?.random()

            val inputStream: InputStream? = randomImageFileName?.let { assetManager.open("$randomRegion/$it") }
            val drawable = Drawable.createFromStream(inputStream, null)

            correctCountryName = randomImageFileName?.removeSuffix(".png")?.replace("_", " ") ?: ""
            binding.drapeauxImageView.setImageDrawable(drawable)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setButtonTextsRandomly() {
        val countryNames = getRandomCountryNames()
        val shuffledCountryNames = countryNames.shuffled().take(numberOfChoices)

        val buttonsLayout = binding.buttonsLinearLayout
        for (i in 0 until buttonsLayout.childCount) {
            val rowLayout = buttonsLayout.getChildAt(i) as LinearLayout
            val button1 = rowLayout.getChildAt(0) as Button
            val button2 = rowLayout.getChildAt(1) as Button

            button1.text = shuffledCountryNames[i * 2]
            button2.text = shuffledCountryNames[i * 2 + 1]
        }

        // Set correct answer randomly among the buttons
        val correctButtonIndex = (0 until numberOfChoices).random()
        val correctRow = correctButtonIndex / 2
        val correctButtonInRow = correctButtonIndex % 2

        when (correctButtonInRow) {
            0 -> (buttonsLayout.getChildAt(correctRow) as LinearLayout).getChildAt(0) as Button
            1 -> (buttonsLayout.getChildAt(correctRow) as LinearLayout).getChildAt(1) as Button
            else -> throw IllegalStateException("Unexpected value for correctButtonInRow: $correctButtonInRow")
        }.text = correctCountryName
    }

    private fun getRandomCountryNames(): List<String> {
        try {
            val assetManager: AssetManager = requireContext().assets
            val availableRegions = selectedRegions ?: resources.getStringArray(R.array.regions_list).toList()
            val countryNamesOptions = mutableListOf<String>()

            for (region in availableRegions) {
                val filesOptions: Array<String>? = assetManager.list(region)
                val imageFilesOptions = filesOptions?.filter { it.endsWith(".png") }
                val countryNamesInRegion =
                    imageFilesOptions?.map { it.removeSuffix(".png").replace("_", " ") } ?: emptyList()
                countryNamesOptions.addAll(countryNamesInRegion)
            }

            return countryNamesOptions.shuffled().take(numberOfChoices)
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun updateQuestionNumber() {
        val questionText =
            resources.getString(R.string.question_number, currentQuestion, totalQuestions)
        binding.numeroQuestionTextView.text = questionText
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
