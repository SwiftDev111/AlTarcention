package celo.urestaurants.models

import celo.urestaurants.constants.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.text.SimpleDateFormat
import java.text.DateFormatSymbols
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepositoryInsight @Inject constructor() {
    private val rtDB: DatabaseReference =
        FirebaseDatabase.getInstance("https://urestaurants-insight.europe-west1.firebasedatabase.app/").reference

    suspend fun getDataInsightFromFirebase(): InsightModel {
        return try {
            val dataSnapshot = rtDB.get().await()
            val insightModel = InsightModel()
            insightModel.insightModels(dataSnapshot)
        } catch (e: Exception) {
            Timber.d("Error: cause ${e.cause}, message: ${e.message}")
            InsightModel()
        }
    }

    fun sendReservationInsideInsight(reservation: Reservation, userID: String, confirmation: () -> Unit) {
        val year = reservation.year
        val month = reservation.month
        val day = reservation.day

        val inputData =  "$day/$month/$year"
        val newData = convertData(inputData)
        val dataMap = mapOf(
            "Email" to reservation.email,
            "Giorno" to newData,
            "Ora" to reservation.hour,
            "Pax" to reservation.numberOfPeople,
            "Request" to reservation.request,
            "Status" to "Confermato",
            "Place" to Constants.m_category.catInfo.nome
        )

        val databasePath = "$userID/Prenotazioni/$newData | ${reservation.hour}"

        rtDB.child("Auth").child(databasePath).setValue(dataMap)

        rtDB.child("Auth").child(databasePath).child("Seggiolini").setValue(reservation.seggiolini)

        confirmation()
    }

    fun getInsightDB() = rtDB

    fun convertData(dataString: String): String {
        val formatInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val data = formatInput.parse(dataString)
        return if (data != null) {
            val symbols = DateFormatSymbols(Locale.getDefault())
            val nomiMesi = symbols.months

            val formatoLocale = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val customSymbols = DateFormatSymbols().apply { this.months = nomiMesi }
            formatoLocale.dateFormatSymbols = customSymbols

            formatoLocale.format(data)
        } else {
            "Ups :-("
        }
    }
}
