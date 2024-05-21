package celo.urestaurants.models

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LanguageHelper @Inject constructor(@ApplicationContext private val context: Context) {

    fun getDeviceLanguage(): String {
        val configuration = context.resources.configuration
        return when (configuration.locales[0].language) {
            "ru" -> "ru" // Lingua russa
            "de" -> "de" // Lingua tedesca
            "en" -> "en" // Lingua inglese
            "es" -> "es" // Lingua spagnola
            "fr" -> "fr" // Lingua francese
            "zh" -> "zh" // Lingua cinese
            else -> "it" // Lingua italiana come default
        }
    }
}
