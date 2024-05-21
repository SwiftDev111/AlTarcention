package celo.urestaurants.models

data class DishModel(
    val title: String,
    val dishType: ProductType,
    val image: Int,
    val catItems: List<MenuAdapterItem.CatItem>
) {
    fun getList(): List<MenuAdapterItem.CatItem> {
        return catItems
    }
}
