package com.machiav3lli.derdiedas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.machiav3lli.derdiedas.R
import com.machiav3lli.derdiedas.data.Noun
import com.machiav3lli.derdiedas.data.getUpdatedNounList
import com.machiav3lli.derdiedas.databinding.FragmentWordBinding
import com.machiav3lli.derdiedas.utils.AnimationUtil.animateButtonDrawable
import com.machiav3lli.derdiedas.utils.AnimationUtil.animateJumpAndSlide
import com.machiav3lli.derdiedas.utils.createNounListFromAsset
import com.machiav3lli.derdiedas.utils.getStringByName
import com.machiav3lli.derdiedas.utils.updateIds

class WordFragment : Fragment(), View.OnClickListener {
    private var currentNoun: Noun? = null
    private var correctGender: String? = null
    private lateinit var binding: FragmentWordBinding
    private val wordActivity: WordActivity
        get() = (requireActivity() as WordActivity)

    private var firstClickBoolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentWordBinding.inflate(inflater, container, false)
        binding.m.setOnClickListener(this)
        binding.n.setOnClickListener(this)
        binding.f.setOnClickListener(this)
        if (wordActivity.allNouns.isNotEmpty())
            currentNoun = wordActivity.allNouns[0]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (currentNoun != null) {
            val noun = currentNoun!!.noun
            correctGender = currentNoun!!.gender
            binding.nounText.text = noun
            binding.translatedText.text = requireContext().getStringByName(noun)
        } else {
            binding.nounText.text = getString(R.string.finished)
            binding.translatedText.text = null
            binding.m.text = getString(R.string.dialog_yes)
            binding.m.setOnClickListener {
                wordActivity.allNouns =
                    requireContext().createNounListFromAsset()
                (requireActivity() as WordActivity).replaceFragment()
            }
            binding.f.text = getString(R.string.dialog_no)
            binding.f.setOnClickListener {
                requireActivity().onBackPressed()
            }
            binding.n.visibility = View.GONE
        }
    }

    override fun onClick(view: View) {
        val pressedButton = view as MaterialButton
        val pressedButtonGender = resources.getResourceEntryName(view.getId())
        if (pressedButtonGender == correctGender && firstClickBoolean) {
            updateList(true)
            pressedButton.backgroundTintList =
                requireContext().getColorStateList(R.color.md_theme_primary)
            pressedButton.setTextColor(requireContext().getColorStateList(R.color.md_theme_onPrimary))
            "${pressedButton.text} ${binding.nounText.text}".also { binding.nounText.text = it }
            binding.nounView.animateJumpAndSlide(requireActivity(), true)
        } else if (firstClickBoolean) {
            updateList(false)
            val correctButton = getCorrectButton(correctGender)
            animateButtonDrawable(correctButton)
            pressedButton.backgroundTintList =
                requireContext().getColorStateList(R.color.md_theme_tertiary)
            pressedButton.setTextColor(requireContext().getColorStateList(R.color.md_theme_onTertiary))
            "${correctButton.text} ${binding.nounText.text}".also { binding.nounText.text = it }
            binding.nounView.animateJumpAndSlide(requireActivity(), false)
        }
        firstClickBoolean = false
    }

    private fun getCorrectButton(correctGender: String?): MaterialButton = when (correctGender) {
        "f" -> binding.f
        "m" -> binding.m
        else -> binding.n
    }

    private fun updateList(isCorrect: Boolean) {
        wordActivity.allNouns = wordActivity.allNouns
            .getUpdatedNounList(currentNoun!!, isCorrect)
            .updateIds()
    }
}