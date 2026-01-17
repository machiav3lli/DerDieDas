package com.machiav3lli.derdiedas.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.machiav3lli.derdiedas.data.NounDatabase
import com.machiav3lli.derdiedas.databinding.ActivityMainBinding
import com.machiav3lli.derdiedas.utils.appTheme
import com.machiav3lli.derdiedas.utils.createNounListFromAsset
import kotlinx.coroutines.runBlocking

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setDayNightTheme(appTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("firstrun", true)) {
            NounDatabase.getInstance(this).let {
                runBlocking {
                    it.nounDao.deleteAll()
                    it.nounDao.insertAll(createNounListFromAsset())
                }
            }
            prefs.edit().putBoolean("firstrun", false).apply()
        }
    }

    override fun onStart() {
        super.onStart()
        setupOnClicks()
    }

    private fun setupOnClicks() {
        binding.practice.setOnClickListener {
            startActivity(Intent(baseContext, WordActivity::class.java))
        }
        binding.stats.setOnClickListener {
            startActivity(Intent(baseContext, StatsActivity::class.java))
        }
        binding.settings.setOnClickListener {
            startActivity(Intent(baseContext, SettingsActivity::class.java))
        }
    }

    private fun setDayNightTheme(theme: String?) {
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}