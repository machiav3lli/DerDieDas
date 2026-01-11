package com.machiav3lli.derdiedas.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.machiav3lli.derdiedas.R
import com.machiav3lli.derdiedas.data.Noun
import com.machiav3lli.derdiedas.data.NounDatabase
import com.machiav3lli.derdiedas.data.WordViewModel
import com.machiav3lli.derdiedas.databinding.ActivityWordBinding

class WordActivity : BaseActivity() {
    private lateinit var binding: ActivityWordBinding
    private lateinit var viewModel: WordViewModel
    var allNouns: MutableList<Noun>
        get() = viewModel.allNouns.value ?: mutableListOf()
        set(value) {
            viewModel.updateAllNouns(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordBinding.inflate(layoutInflater)
        val nounDao = NounDatabase.getInstance(this).nounDao
        val viewModelFactory = WordViewModel.Factory(nounDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[WordViewModel::class.java]
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, WordFragment()).commit()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.back.setOnClickListener { onBackPressed() }
    }

    fun replaceFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        fragmentTransaction.replace(R.id.fragment_container, WordFragment())
        fragmentTransaction.commit()
    }
}