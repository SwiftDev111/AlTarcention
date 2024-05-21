package celo.urestaurants.models

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataCacheManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val PREFERENCES_NAME = "DataCachePreferences"
    private val KEY_CACHED_DATA = "cachedData"
    private val KEY_TIMESTAMP = "timestamp"

    fun saveDataToCache(data: List<CategoryModel>) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putString(KEY_CACHED_DATA, CategoryModel.toJson(data))

        editor.putLong(KEY_TIMESTAMP, System.currentTimeMillis())

        editor.apply()
    }

    fun getCachedData(): List<CategoryModel>? {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val cachedData = preferences.getString(KEY_CACHED_DATA, null)
        val timestamp = preferences.getLong(KEY_TIMESTAMP, 0)

        if (cachedData != null && isCacheValid(timestamp)) {
            return CategoryModel.fromJson(cachedData)
        }

        return null
    }

    private fun isCacheValid(timestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val cacheDuration = TimeUnit.DAYS.toMillis(7) // Cache valida per 7 giorni

        return currentTime - timestamp < cacheDuration
    }

    fun updateCachedItem(updatedItem: CategoryModel) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()

        // Recupera i dati esistenti dalla cache
        val cachedData = preferences.getString(KEY_CACHED_DATA, null)
        val timestamp = preferences.getLong(KEY_TIMESTAMP, 0)

        if (cachedData != null && isCacheValid(timestamp)) {
            // Se la cache è valida, deserializza i dati e cerca l'elemento da aggiornare
            val cachedList = CategoryModel.fromJson(cachedData)
            val updatedList = cachedList?.size?.let { ArrayList<CategoryModel>(it) }

            if (cachedList != null) {
                for (item in cachedList) {
                    if (item.catKey == updatedItem.catKey) {
                        // Aggiorna l'elemento
                        updatedList?.add(updatedItem)
                    } else {
                        // Mantieni gli altri elementi invariati
                        updatedList?.add(item)
                    }
                }
            }

            // Salva la lista aggiornata nella cache
            editor.putString(KEY_CACHED_DATA, updatedList?.let { CategoryModel.toJson(it) })
            editor.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            editor.apply()
        }
    }

}
