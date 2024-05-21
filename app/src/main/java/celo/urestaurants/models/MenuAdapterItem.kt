package celo.urestaurants.models

import android.util.Log
import com.google.firebase.database.DataSnapshot


sealed class MenuAdapterItem {
    data class Section(val title: String) : MenuAdapterItem()

    data class CatItem(
        var key: String? = "",
        var nome: String? = "",
        var nomeRu: String? = "",
        var nomeDe: String? = "",
        var nomeEn: String? = "",
        var nomeEs: String? = "",
        var nomeFr: String? = "",
        var nomeZh: String? = "",
        var ingredienti: String? = "",
        var ingredientiRu: String? = "",
        var ingredientiDe: String? = "",
        var ingredientiEn: String? = "",
        var ingredientiEs: String? = "",
        var ingredientiFr: String? = "",
        var ingredientiZh: String? = "",
        var priceD: String? = "",
        var priceM: String? = "",
        var sezione: String? = "",
        var sezioneRu: String? = "",
        var sezioneDe: String? = "",
        var sezioneEn: String? = "",
        var sezioneEs: String? = "",
        var sezioneFr: String? = "",
        var sezioneZh: String? = "",
        var subCat: String? = ""
    ) : MenuAdapterItem() {
        var language = "it"
        var isFirst: Boolean = false
        var isLast: Boolean = false
        fun getItem(snapshot: DataSnapshot) {
            key = snapshot.key
            for (snap in snapshot.children) {
                val subKey = snap.key
                val snapValue = snap.getValue(String::class.java)
                if (subKey != null) {
                    when (subKey) {
                        "000-Nome" -> {
                            nome = snapValue
                        }

                        "000-Nome!RU" -> {
                            nomeRu = snapValue
                        }

                        "000-Nome!de" -> {
                            nomeDe = snapValue
                        }

                        "000-Nome!en" -> {
                            nomeEn = snapValue
                        }

                        "000-Nome!es" -> {
                            nomeEs = snapValue
                        }

                        "000-Nome!fr" -> {
                            nomeFr = snapValue
                        }

                        "000-Nome!zh" -> {
                            nomeZh = snapValue
                        }

                        "ingredienti" -> {
                            ingredienti = snapValue
                        }

                        "ingredienti!RU" -> {
                            ingredientiRu = snapValue
                        }

                        "ingredienti!de" -> {
                            ingredientiDe = snapValue
                        }

                        "ingredienti!en" -> {
                            ingredientiEn = snapValue
                        }

                        "ingredienti!es" -> {
                            ingredientiEs = snapValue
                        }

                        "ingredienti!fr" -> {
                            ingredientiFr = snapValue
                        }

                        "ingredienti!zh" -> {
                            ingredientiZh = snapValue
                        }

                        "priceD" -> {
                            priceD = snapValue
                        }

                        "priceM" -> {
                            priceM = snapValue
                        }

                        "sezione" -> {
                            sezione = snapValue
                        }

                        "sezione!RU" -> {
                            sezioneRu = snapValue
                        }

                        "sezione!de" -> {
                            sezioneDe = snapValue
                        }

                        "sezione!en" -> {
                            sezioneEn = snapValue
                        }

                        "sezione!es" -> {
                            sezioneEs = snapValue
                        }

                        "sezione!fr" -> {
                            sezioneFr = snapValue
                        }

                        "sezione!zh" -> {
                            sezioneZh = snapValue
                        }

                        "subCategory" -> {
                            subCat = snapValue
                        }
                    }
                }
            }
        }
    }

}