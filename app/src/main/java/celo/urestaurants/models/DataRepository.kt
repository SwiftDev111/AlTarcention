package celo.urestaurants.models

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor() {
    private val rtDB: DatabaseReference = FirebaseDatabase.getInstance().reference

    suspend fun getDataFromFirebase(): List<CategoryModel> {
        return try {
            val dataSnapshot = rtDB.get().await()
            val catModel = CategoryModel()
            catModel.loadData(dataSnapshot)
        } catch (e: Exception) {
            mutableListOf()
        }
    }
}