package com.example.myapplication.ui.theme

import android.content.Context
import android.content.SharedPreferences

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "algoquest_theme_prefs"
        private const val KEY_THEME_MODE = "app_theme_mode"
        private const val KEY_SYNTAX_SCHEME = "syntax_scheme"
        private const val KEY_EDITOR_FONT = "editor_font"

        @Volatile
        private var instance: ThemePreferences? = null

        fun getInstance(context: Context): ThemePreferences {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferences(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        return try {
            AppThemeMode.valueOf(name)
        } catch (_: Exception) {
            AppThemeMode.DARK
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getSyntaxScheme(): String {
        return prefs.getString(KEY_SYNTAX_SCHEME, "Neon Synth") ?: "Neon Synth"
    }

    fun setSyntaxScheme(scheme: String) {
        prefs.edit().putString(KEY_SYNTAX_SCHEME, scheme).apply()
    }

    fun getEditorFont(): String {
        return prefs.getString(KEY_EDITOR_FONT, "JetBrains Mono") ?: "JetBrains Mono"
    }

    fun setEditorFont(font: String) {
        prefs.edit().putString(KEY_EDITOR_FONT, font).apply()
    }
}
