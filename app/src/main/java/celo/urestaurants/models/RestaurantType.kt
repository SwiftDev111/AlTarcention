package celo.urestaurants.models

enum class RestaurantType(private val value: String) {
    RESTAURANT("Ristorante"),
    PIZZA("Pizza"),
    BAR("Bar");

    override fun toString(): String {
        return value
    }
}