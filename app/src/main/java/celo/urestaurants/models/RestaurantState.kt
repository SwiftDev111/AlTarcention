package celo.urestaurants.models

enum class RestaurantState(private val state: String) {
    OPENED("Aperto"),
    CLOSED("Chiuso");

    override fun toString(): String {
        return state
    }
}