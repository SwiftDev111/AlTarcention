package celo.urestaurants.models

import celo.urestaurants.constants.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepositoryReservations @Inject constructor() {
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://urestaurants-reservations.europe-west1.firebasedatabase.app/").reference

    fun getDataReservationsFromFirebase(status: (String) -> Unit) {
        try {
            val reservations = databaseReference.child("${Constants.m_category.catKey}S")

            reservations.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (childSnapshot in dataSnapshot.children) {
                        if (childSnapshot.key == "status") {
                            Timber.d("status : ${childSnapshot.value}")
                            val statusValue = childSnapshot.value as String
                            status.invoke(statusValue)
                            return
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Timber.e("XXXX | onCancelled DatabaseError: ${databaseError.message}")
                }
            })
        } catch (e: Exception) {
            Timber.d("Error: cause ${e.cause}, message: ${e.message}")
            status.invoke("nope")
        }
    }

    fun sendReservation(reservation: Reservation, confirmation: () -> Unit) {
        val year = reservation.year
        val month = reservation.month
        val day = reservation.day

        val nameSurname = reservation.nameSurname + " !" + generateRandomString()

        val dataMap = mapOf(
            "Cognome" to reservation.surname,
            "Ora" to reservation.hour,
            "Pax" to reservation.numberOfPeople,
            "Request" to reservation.request,
            "Seggiolini" to reservation.seggiolini,
            "Status" to reservation.status
        )

        val databasePath = "${Constants.m_category.catKey}R/$year/$month/$day/$nameSurname"

        databaseReference.child(databasePath).setValue(dataMap)

        confirmation()
    }

    private fun generateRandomString(length: Int = 5): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
