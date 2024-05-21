package celo.urestaurants.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import celo.urestaurants.constants.Constants
import celo.urestaurants.models.CategoryModel
import celo.urestaurants.models.DataCacheManager
import celo.urestaurants.models.DataRepository
import celo.urestaurants.models.NetworkManager
import celo.urestaurants.models.RestaurantType
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DataRepository,
    private val dataCacheManager: DataCacheManager,
    private val networkManager: NetworkManager
) : ViewModel() {
    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _categories = MutableLiveData<List<CategoryModel>?>()
    val categories: MutableLiveData<List<CategoryModel>?> get() = _categories

    private val _updateItem = MutableLiveData<CategoryModel>()
    val updateItem: LiveData<CategoryModel> get() = _updateItem
    fun getCategories(
        filterKeys: List<String>,
        modeKey: RestaurantType
    ): List<CategoryModel> {
        val categoryModels = Constants.m_categories
        val location = mutableListOf<CategoryModel>()
        categoryModels.forEach { category ->
            val mode = category.catInfo.mode
            val atr1 = category.catInfo.atr1
            val atr2 = category.catInfo.atr2
            val isActive = category.catInfo.isActive
            Timber.d("Parsing: ${category.catInfo.nome} | ${category.catInfo.isActive} | ${category.catInfo.mode} | ${category.catInfo.atr1} | ${category.catInfo.atr2}")

            if (mode == modeKey.toString()) {
                if (filterKeys.isEmpty()) {
                    if (isActive) location.add(category)
//                    if (atr1.isNotEmpty() && atr2.isNotEmpty()) {
//                        if (isActive) location.add(category)
//                    }
                }
                else {
                    var found = false
                    if (atr1.isNotEmpty()) {
                            for (key in filterKeys) {
                            if (atr1 == key) {
                                if (isActive) location.add(category)
                                found = true
                                break
                            }
                        }
                    }
                    if (!found && atr2.isNotEmpty()) {
                        for (key in filterKeys) {
                            if (atr2 == key) {
                                location.add(category)
                                break
                            }
                        }
                    }
                }
            }
        }
        return location
    }

    fun loadDataFromCacheOrNetwork() {
        viewModelScope.launch {
            val cachedData = dataCacheManager.getCachedData()

            if (!cachedData.isNullOrEmpty()) {
                Timber.d("We have a cache, so show it")
                _categories.value = cachedData

                if (networkManager.isNetworkAvailable()) {
                    checkVersions(cachedData)
                } else {
                    Timber.d("Network not available, skipping network check")
                }
            } else {
                Timber.d("No data from cache, loading from network")
                val networkData = fetchDataFromNetwork()
                dataCacheManager.saveDataToCache(networkData)
                _categories.value = networkData
            }
        }
    }

    private suspend fun checkVersions(cachedData: List<CategoryModel>) {
        Timber.d("Now check versions and update if needed")
        val networkData = fetchDataFromNetwork()

        if (isDataDifferent(cachedData, networkData)) {
            Timber.d("Data is different, updating from network")
            _categories.value = networkData
            dataCacheManager.saveDataToCache(networkData)
        } else {
            Timber.d("Data is the same, loaded from cache")
        }
    }

    private suspend fun fetchDataFromNetwork(): List<CategoryModel> {
        val newData = repository.getDataFromFirebase()
        dataCacheManager.saveDataToCache(newData)
        return newData
    }

    private fun isDataDifferent(
        cachedData: List<CategoryModel>,
        networkData: List<CategoryModel>
    ): Boolean {
        if (cachedData.size != networkData.size) {
            return true
        }

        for (i in cachedData.indices) {
            if (cachedData[i].version != networkData[i].version) {
                return true
            }
        }

        return false
    }

    fun updateDataCache(category: CategoryModel) {
        viewModelScope.launch {
            dataCacheManager.updateCachedItem(category)
            _updateItem.value = category
        }
    }
}