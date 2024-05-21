package celo.urestaurants.models

import com.google.firebase.database.DataSnapshot
import timber.log.Timber

class CatInfoModel {
    var catId = ""
    var m_monday = ""
    var m_tuesday = ""
    var m_wednesday = ""
    var m_thursday = ""
    var m_friday = ""
    var m_saturday = ""
    var m_sunday = ""
    var citta = ""
    var city = ""

    @JvmField
    var atr1 = ""

    @JvmField
    var atr2 = ""

    @JvmField
    var logoUrl = ""

    var uriList: List<String> = listOf()

    @JvmField
    var mode = ""

    @JvmField
    var nome = ""

    @JvmField
    var star = ""
    var telefono = ""
    var urlGMap = ""
    var urlMap = ""
    var version = ""
    var isActive = false
    fun loadInfoData(snapshot: DataSnapshot) {
        for (snap in snapshot.children) {
            val subKey = snap.key
            try {
                val snapValue = snap.value
                if (subKey != null) {
                    if (snapValue is String) {
                        when (subKey) {
                            "001-Monday" -> m_monday = snapValue
                            "002-Tuesday" -> m_tuesday = snapValue
                            "003-Wednesday" -> m_wednesday = snapValue
                            "004-Thursday" -> m_thursday = snapValue
                            "005-Friday" -> m_friday = snapValue
                            "006-Saturday" -> m_saturday = snapValue
                            "007-Sunday" -> m_sunday = snapValue
                            "Citta" -> citta = snapValue
                            "atr1" -> atr1 = snapValue
                            "atr2" -> atr2 = snapValue
                            "id" -> catId = snapValue
                            "mode" -> mode = snapValue
                            "nome" -> nome = snapValue
                            "star" -> star = snapValue
                            "telefono" -> telefono = snapValue
                            "urlGMaps" -> urlGMap = snapValue
                            "urlMaps" -> urlMap = snapValue
                            "version" -> version = snapValue
                            "logoUrl" -> logoUrl = snapValue
                            "city" -> city = snapValue
                        }
                    }
                    if(snapValue is Boolean) {
                        when (subKey) {
                            "isActive" -> isActive = snapValue
                        }
                    }
                    if (snapValue is List<*>) {
                        when (subKey) {
                            // Assuming URI list is stored as a list of strings
                            "uriList" -> uriList = snapValue.filterIsInstance<String>()
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error parsing snapshot: ${e.message}")
            }
        }
    }
}
