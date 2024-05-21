package celo.urestaurants.ui.home

import androidx.lifecycle.ViewModel
import celo.urestaurants.models.DataCacheManager
import celo.urestaurants.models.DishModel
import celo.urestaurants.models.LanguageHelper
import celo.urestaurants.models.MenuAdapterItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val languageHelper: LanguageHelper,
    private val dataCacheManager: DataCacheManager,
) : ViewModel() {

    var startingList: MutableList<MenuAdapterItem> = mutableListOf()
    var menuList: MutableList<MenuAdapterItem> = mutableListOf()
    val sectionTitles = mutableListOf<String>()
    val dishModels = mutableListOf<DishModel>()
    var selected: DishModel? = null
    fun getLanguage() = languageHelper.getDeviceLanguage()
    fun getCachedData() = dataCacheManager.getCachedData()

    fun assignFirstLastFlags(list: List<MenuAdapterItem>): List<MenuAdapterItem> {
        var lastAIndex = -1

        for ((index, item) in list.withIndex()) {
            when (item) {
                is MenuAdapterItem.Section -> lastAIndex = index
                is MenuAdapterItem.CatItem -> {
                    item.isFirst = lastAIndex != -1 && (index == lastAIndex + 1 || list[index - 1] is MenuAdapterItem.Section)
                    item.isLast = index < list.size - 1 && (list[index + 1] is MenuAdapterItem.Section || (index == lastAIndex + 1 && list[index + 1] is MenuAdapterItem.Section))
                }
            }
        }
        val filteredList = list.filterIndexed { index, item ->
            !(index == 0 && item is MenuAdapterItem.Section && item.title.isBlank())
        }
        return filteredList
    }
}