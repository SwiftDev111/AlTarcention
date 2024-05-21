package celo.urestaurants.models

data class Reservation(
    val nameSurname: String = "N/A",
    val surname: String= "N/A",
    val email: String = "N/A",
    val year: String = "N/A",
    val month: String = "N/A",
    val day: String = "N/A",
    val hour: String = "N/A",
    val numberOfPeople: String = "N/A",
    val request: String = "N/A",
    val status: String = "Stampato",
    val seggiolini: String = "N/A",
)