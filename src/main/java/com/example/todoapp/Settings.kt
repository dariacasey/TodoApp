package com.example.todoapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.example.todoapp.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides default header
        supportActionBar?.hide()

        // Reads if app is in dark mode or not
        sharedPref = getSharedPreferences("prefs", MODE_PRIVATE)
        val nightMode = sharedPref.getBoolean("darkMode", false)

        // Checks switch if it is in dark mode
        if (nightMode) {
            binding.darkMode.isChecked = true
        }

        // When switch is checked, app is put in dark mode and "darkMode", false is put into
        // preferences file
        binding.darkMode.setOnClickListener {
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor = sharedPref.edit()
                editor.putBoolean("darkMode", false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor = sharedPref.edit()
                editor.putBoolean("darkMode", true)
            }
            // Applies changes to preferences file
            editor.apply()
        }
        sharedPref.edit().putBoolean("darkMode", binding.darkMode.isChecked)

        // When back button is hit, user is taken back to the main activity
        binding.backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}