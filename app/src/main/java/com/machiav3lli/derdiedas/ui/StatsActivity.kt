package com.machiav3lli.derdiedas.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.machiav3lli.derdiedas.R
import com.machiav3lli.derdiedas.databinding.ActivityStatsBinding
import com.machiav3lli.derdiedas.utils.getNounsCount
import kotlinx.coroutines.runBlocking
import java.io.UnsupportedEncodingException
import java.util.Locale

class StatsActivity : BaseActivity() {
    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            setWordStats()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        binding.back.setOnClickListener { onBackPressed() }
        binding.statsInfo.setOnClickListener { onFullWords() }
    }

    // TODO replace within the Composable screen
    @Throws(UnsupportedEncodingException::class)
    private fun setWordStats() {
        Thread {
            runBlocking {
                val (learnedWords, allNouns) = this@StatsActivity.getNounsCount()
                runOnUiThread {
                    findViewById<TextView>(R.id.word_stats).text =
                        String.format(Locale.ENGLISH, "%d / %d", learnedWords, allNouns)
                }
            }
        }.start()
    }

    private fun onFullWords() {
        val titleString = resources.getString(R.string.full_words_title)
        val textString = resources.getString(R.string.full_words_text)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle(titleString)
        builder.setMessage(textString)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }
}