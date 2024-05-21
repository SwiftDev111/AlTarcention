package celo.urestaurants.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import celo.urestaurants.models.DataRepositoryInsight
import celo.urestaurants.models.InsightModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataRepositoryInsight: DataRepositoryInsight
) : ViewModel() {
    var selectedTab: Int = 0

    private val _insightModelMutableLiveData = MutableLiveData<InsightModel>()
    val insightModelMutableLiveData: MutableLiveData<InsightModel> get() = _insightModelMutableLiveData


    private val _storeNodeId = MutableLiveData<String>()
    val storeNodeId: MutableLiveData<String> get() = _storeNodeId

    private val _storeAuthNodeId = MutableLiveData<String>()
    val storeAuthNodeId: MutableLiveData<String> get() = _storeAuthNodeId

    fun getInsight() {
        viewModelScope.launch {
            val insight = dataRepositoryInsight.getDataInsightFromFirebase()
            _insightModelMutableLiveData.postValue(insight)
        }
    }


    fun updateRecordInsightApp(nodeId: String?, recordInAppInsight: InsightModel.InsightApp?) {
        val refTest = dataRepositoryInsight.getInsightDB().child("App")
        refTest.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var nodeExist = false

                for (childSnapshot in dataSnapshot.children) {
                    val existingInsightApp =
                        childSnapshot.getValue(InsightModel.InsightApp::class.java)

                    if (existingInsightApp != null && childSnapshot.key == nodeId) {
                        childSnapshot.ref.setValue(recordInAppInsight)
                        return
                    }

                    nodeExist = true
                }

                if (nodeExist) {
                    val newNodeUnderTest = refTest.push()
                    recordInAppInsight?.id = newNodeUnderTest.key ?: ""
                    newNodeUnderTest.setValue(recordInAppInsight)
                    _storeNodeId.postValue(newNodeUnderTest.key)
                } else {
                    Timber.e("XXXX | Nodo not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e("XXXX | onCancelled DatabaseError: ${databaseError.message}")
            }
        })
    }


    fun updateRecordInsightAuth(nodeId: String?, recordInAuthInsight: InsightModel.InsightAuth?) {

        val refTest = dataRepositoryInsight.getInsightDB().child("Auth")

        refTest.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var nodeExist = false

                for (childSnapshot in dataSnapshot.children) {
                    val existingInsightApp =
                        childSnapshot.getValue(InsightModel.InsightAuth::class.java)

                    if (existingInsightApp != null && existingInsightApp.uuid == nodeId) {
                        // Sovrascrivo e perdo le prenotazioni
                        //childSnapshot.ref.setValue(recordInAuthInsight)
                        _storeAuthNodeId.postValue(childSnapshot.key)
                        return
                    }

                    nodeExist = true
                }

                if (nodeExist) {
                    val newNodeUnderTest = refTest.push()
                    recordInAuthInsight?.id = newNodeUnderTest.key ?: ""
                    newNodeUnderTest.setValue(recordInAuthInsight)
                    _storeAuthNodeId.postValue(newNodeUnderTest.key)
                } else {
                    Timber.e("XXXX | Nodo not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e("XXXX | onCancelled DatabaseError: ${databaseError.message}")
            }
        })
    }
}