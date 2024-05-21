package celo.urestaurants.ui.ordinazioni

import androidx.lifecycle.ViewModel
import celo.urestaurants.models.DataCacheManager
import celo.urestaurants.models.DataRepositoryInsight
import celo.urestaurants.models.DataRepositoryReservations
import celo.urestaurants.models.Reservation
import celo.urestaurants.models.SimpleTextModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val dataCacheManager: DataCacheManager,
    private val dataRepositoryReservations: DataRepositoryReservations,
    private val dataRepositoryInsight: DataRepositoryInsight
) : ViewModel() {

    var numbersOfPeople: Int = -1
    var timeToOrder: String? = null
    var selectedDate: String? = null
    var seggiolini: String? = null
    var specialRequest: String? = null
    fun getCachedData() = dataCacheManager.getCachedData()

    fun restaurantCanMakeReservation(status: (String) -> Unit) {
        dataRepositoryReservations.getDataReservationsFromFirebase(status)
    }

    fun confirmReservation(reservation: Reservation, userID: String, confirmation: () -> Unit) {
        dataRepositoryReservations.sendReservation(reservation) {
            dataRepositoryInsight.sendReservationInsideInsight(reservation, userID, confirmation)
        }
    }

    fun generateNumberOfPeopleList(): List<SimpleTextModel> {
        val list = mutableListOf<SimpleTextModel>()
        for (i in 1..20) {
            list.add(SimpleTextModel(titleText = i.toString()))
        }
        return list
    }

    fun generateTimesList(selectedDate: Calendar): List<SimpleTextModel> {
        val list = mutableListOf<SimpleTextModel>()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

        calendar.time = selectedDate.time
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 30)

        val currentTime = Calendar.getInstance()

        while (calendar.get(Calendar.HOUR_OF_DAY) < 23) {
            if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)
                && selectedDate.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR)) {
                if (currentTime.after(calendar) && currentTime.get(Calendar.HOUR_OF_DAY) < 23) {
                    calendar.add(Calendar.MINUTE, 30)
                    continue
                }
            } else {
                if (calendar.get(Calendar.HOUR_OF_DAY) >= 23) {
                    break
                }
            }

            val formattedTime = sdf.format(calendar.time)
            list.add(SimpleTextModel(titleText = formattedTime))
            calendar.add(Calendar.MINUTE, 30)
        }

        return list
    }


    fun formatDate(localDate: LocalDate): String {
        // Creazione di un formato per la data
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Formattazione della data e restituzione come stringa
        return localDate.format(formatter)
    }

    fun clearData() {
        numbersOfPeople = -1
        timeToOrder = null
        selectedDate = null
        seggiolini = null
        specialRequest = null
    }

}
