package com.example.metronome

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(appContext: AppCompatActivity) {

    companion object {
        lateinit var shared: ThemeManager
    }

    private val context = appContext
    private val sharedPref = context.getPreferences(Context.MODE_PRIVATE)

    fun loadSystemTheme() = setSystemTheme(getSystemTheme())

    private fun getSystemTheme(): String {
        return sharedPref.getString(
            context.getString(R.string.theme_value),
            context.getString(R.string.theme_default_value)
        ) ?: ""
    }

    fun setSystemTheme(newTheme: String) {
        val setTheme = {id: Int -> AppCompatDelegate.setDefaultNightMode(id)}
        when (newTheme) {
            "Light" -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark"  -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
            else    -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        context.delegate.applyDayNight()
        saveSystemTheme(newTheme)
    }

    private fun saveSystemTheme(newTheme: String) {
        with (sharedPref.edit()) {
            putString(context.getString(R.string.theme_value), newTheme)
            apply()
        }
    }

}