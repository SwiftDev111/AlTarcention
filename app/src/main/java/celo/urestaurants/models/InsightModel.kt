package celo.urestaurants.models

import com.google.firebase.database.DataSnapshot
import timber.log.Timber

data class InsightModel(
    val insightAppList: MutableList<InsightApp> = mutableListOf(),
    val insightAuthList: MutableList<InsightAuth> = mutableListOf(),
    val insightBusinessList: MutableList<InsightBusiness> = mutableListOf()
) {

    fun insightModels(snapshot: DataSnapshot): InsightModel {
        for (snapData in snapshot.children) {
            val key = snapData.key
            when (key) {
                "App" -> {
                    for (snap in snapData.children) {
                        val subKey = snap.key
                        val insightApp = InsightApp()
                        insightApp.id = subKey as String
                        for (snapChild in snap.children) {
                            if (snapChild != null) {
                                when (snapChild.key) {

                                    "batteria" -> {
                                        insightApp.battery = snapChild.value.toString()
                                    }

                                    "modelloTelefono" -> {
                                        insightApp.model = snapChild.value as String
                                    }

                                    "Versione" -> {
                                        insightApp.appVersion = snapChild.value as String
                                    }

                                    "Lingua" -> {
                                        insightApp.language = snapChild.value as String
                                    }

                                    "NomeTelefono" -> {
                                        insightApp.telephoneType = snapChild.value as String
                                    }

                                    "ios" -> {
                                        insightApp.osVersion = snapChild.value as String
                                    }

                                    "Accessi" -> {
                                        insightApp.lastAccess =
                                            (snapChild.value as HashMap<String, String>).map { it.key }
                                    }
                                }


                            } else {
                                Timber.d("Value is null")
                            }
                        }
                        Timber.d("XXXX | $insightApp")
                        insightAppList.add(insightApp)
                    }
                }

                "Auth" -> {
                    for (snap in snapData.children) {
                        val subKey = snap.key
                        val insightAuth = InsightAuth()
                        insightAuth.id = subKey as String
                        for (snapChild in snap.children) {
                            if (snapChild != null) {
                                when (snapChild.key) {
                                    "Cognome" -> {
                                        insightAuth.surname = snapChild.value as String
                                    }

                                    "Nome" -> {
                                        insightAuth.name = snapChild.value as String
                                    }

                                    "Email" -> {
                                        insightAuth.email = snapChild.value as String
                                    }

                                    "UUID" -> {
                                        insightAuth.uuid = snapChild.value as String
                                    }
                                }


                            } else {
                                Timber.d("Value is null")
                            }
                        }

                        insightAuthList.add(insightAuth)
                    }
                }

                "Business" -> {
                    for (snap in snapData.children) {
                        val subKey = snap.key
                        val insightBusiness = InsightBusiness()
                        insightBusiness.id = subKey as String
                        for (snapChild in snap.children) {
                            if (snapChild != null) {
                                when (snapChild.key) {
                                    "Accessi" -> {
                                        insightBusiness.lastAccess =
                                            (snapChild.value as HashMap<String, String>).map { it.key }
                                    }

                                    "FCMToken" -> {
                                        insightBusiness.fcmToken = snapChild.value as String
                                    }

                                    "Lingua" -> {
                                        insightBusiness.language = snapChild.value as String
                                    }

                                    "Nome" -> {
                                        insightBusiness.name = snapChild.value as String
                                    }

                                    "NomeTelefono" -> {
                                        insightBusiness.telephoneType = snapChild.value as String
                                    }

                                    "Versione" -> {
                                        insightBusiness.appVersion = snapChild.value as String
                                    }

                                    "batteria" -> {
                                        insightBusiness.battery = snapChild.value.toString()
                                    }

                                    "ios" -> {
                                        insightBusiness.osVersion = snapChild.value as String
                                    }

                                }

                            } else {
                                Timber.d("Value is null")
                            }
                        }

                        insightBusinessList.add(insightBusiness)
                    }
                }
            }
        }

        return InsightModel(insightAppList)
    }

    data class InsightApp(
        var lastAccess: List<String> = listOf(),
        var language: String = "",
        var telephoneType: String = "",
        var appVersion: String = "",
        var battery: String = "",
        var osVersion: String = "",
        var model: String = "",
    ) {
        var id: String = ""
    }

    data class InsightAuth(
        var surname: String = "",
        var name: String = "",
        var email: String = "",
        var uuid: String = "",
    ) {
        var id: String = ""
    }

    data class InsightBusiness(
        var lastAccess: List<String> = listOf(),
        var fcmToken: String = "",
        var language: String = "",
        var name: String = "",
        var telephoneType: String = "",
        var appVersion: String = "",
        var battery: String = "",
        var osVersion: String = "",
    ) {
        var id: String = ""
    }

}
