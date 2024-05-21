package celo.urestaurants.models

import android.content.Context
import android.util.Log
import celo.urestaurants.constants.Constants
import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class CategoryModel(
    var deviceId: String? = "",
    @JvmField
    var catKey: String? = "",
    var catConfig: CatConfig = CatConfig(),
    @JvmField
    var catInfo: CatInfoModel = CatInfoModel(),
    var catItems: MutableList<MenuAdapterItem.CatItem> = mutableListOf(),
    @JvmField
    var favorites: MutableList<CatFavoriteModel> = mutableListOf(),
    var version: String = "1.0.0",
) {
    init {
        // Inizializzazione degli oggetti all'interno del costruttore
        catConfig = CatConfig()
        catInfo = CatInfoModel()
        catItems = mutableListOf()
        favorites = mutableListOf()
    }

    companion object {
        fun toJson(data: List<CategoryModel>): String {
            val gson = Gson()
            return gson.toJson(data)
        }

        fun fromJson(json: String): List<CategoryModel>? {
            val gson = Gson()
            val type = object : TypeToken<List<CategoryModel>>() {}.type
            return gson.fromJson<List<CategoryModel>>(json, type)
        }
    }

    fun loadData(snapshot: DataSnapshot): List<CategoryModel> {
        val categoryList = mutableListOf<CategoryModel>()
        for (childSnapshot in snapshot.children) {
            val category = CategoryModel()
            category.catKey = childSnapshot.key
            for (snapData in childSnapshot.children) {
                val key = snapData.key
                when (key) {
                    "Config" -> {
                        category.catConfig.loadSectionsData(snapData)
                    }
                    "Info" -> {
                        category.catInfo.loadInfoData(snapData)
                    }
                    "Items" -> {
                        for (snap in snapData.children) {
                            val item = MenuAdapterItem.CatItem()
                            item.getItem(snap)
                            category.catItems.add(item)
                        }
                    }
                    "Favorites" -> {
                        category.loadFavoritesData(snapData)
                    }
                }
            }
           /* if (category.catInfo.atr1 != "") {
                if (category.catInfo.atr2 != "") {
                    categoryList.add(category)
                }
            }*/

//            if (category.catInfo.isActive){
                categoryList.add(category)
               /* if (category.catInfo.atr1 != ""){
                    categoryList.add(category)
                }else if (category.catInfo.atr2 != ""){
                    categoryList.add(category)
                }else if (category.catInfo.atr1 == "" && category.catInfo.atr2 != ""){
                    categoryList.add(category)
                }else if(category.catInfo.atr2 == "" && category.catInfo.atr1 != ""){
                    categoryList.add(category)
                }else if (category.catInfo.atr1 == "" && category.catInfo.atr2 == ""){
                    categoryList.add(category)
                }*/
//            }

        }
        return categoryList
    }


    private fun loadFavoritesData(snapshot: DataSnapshot) {
        favorites = mutableListOf()
        for (snap in snapshot.children) {
            val fav = CatFavoriteModel()
            fav.deviceId = snap.key.toString()
            for (snapData in snap.children) {
                val subKey = snapData.key
                val snalValue = snapData.getValue(String::class.java)
                if (subKey != null) {
                    if (subKey == "favorite") {
                        if (snalValue != null) {
                            fav.favorite = snalValue
                        }
                    }
                }
            }
            favorites.add(fav)
        }
    }

    fun saveNomeToSharedPreferences(context: Context) {
        val nome = catInfo.nome // Get the value of nome from catInfo
        // Save nome into SharedPreferences
        val sharedPreferences = context.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("nome_key", nome)
        editor.apply()
    }
}
