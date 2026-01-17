package com.machiav3lli.derdiedas.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.machiav3lli.derdiedas.data.NounDatabase
import com.machiav3lli.derdiedas.data.WordViewModel
import com.machiav3lli.derdiedas.ui.theme.AppTheme

class WordActivity : BaseActivity() {
    private lateinit var viewModel: WordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nounDao = NounDatabase.getInstance(this).nounDao
        val viewModelFactory = WordViewModel.Factory(nounDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[WordViewModel::class.java]

        setContent {
            AppTheme {
                WordScreen(
                    viewModel = viewModel,
                    onBack = { onBackPressed() }
                )
            }
        }
    }
}