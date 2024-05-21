package celo.urestaurants.models

import celo.urestaurants.R

enum class ProductType(private val value: String, private val image: Int) {
    ANTIPASTI(value = "Antipasti", image = R.drawable.antipasti),
    AMARI(value = "Amari", image = R.drawable.amari),
    BEVANDE(value = "Bevande", image = R.drawable.bevande),
    BIRRE(value = "Birre", image = R.drawable.birre),
    BRIOCHES(value = "Brioches", image = R.drawable.brioches),
    CAFFE(value = "Caffe", image = R.drawable.caffe),
    CAFFETTERIA(value = "Caffetteria", image = R.drawable.caffe),
    COCKTAIL(value = "Cocktail", image = R.drawable.cocktails),
    CONTORNI(value = "Contorni", image = R.drawable.contorni),
    CUCINA(value = "Cucina", image = R.drawable.primi),
    DOLCI(value = "Dolci", image = R.drawable.dolci),
    GINVODKA(value = "Gin & Vodka", image = R.drawable.cocktails),
    KEBAB(value = "Kebab", image = R.drawable.kebab),
    PIZZE(value = "Pizze", image = R.drawable.pizze),
    PRIMI(value = "Primi", image = R.drawable.primi),
    SECONDI(value = "Secondi", image = R.drawable.secondi),
    SUCCHI(value = "Succhi", image = R.drawable.succhi),
    SPUNTINI(value = "Spuntini", image = R.drawable.hot_dog),
    VINO(value = "Vino", image = R.drawable.vino);

    override fun toString(): String {
        return value
    }

    fun getImage() : Int {
        return image
    }
}