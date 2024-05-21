package celo.urestaurants.ui.informations

import androidx.lifecycle.ViewModel
import celo.urestaurants.models.DataCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val dataCacheManager: DataCacheManager
) : ViewModel() {

    fun getCachedData() = dataCacheManager.getCachedData()

}