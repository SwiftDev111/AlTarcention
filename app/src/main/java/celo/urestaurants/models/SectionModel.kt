package celo.urestaurants.models

import android.util.Log
import com.google.firebase.database.DataSnapshot

class SectionModel {
    var key: String? = ""
    var name: String? = ""
    var type: String? = ""
    fun loadSection(snapshot: DataSnapshot) {
        key = snapshot.key
        for (snap in snapshot.children) {
            val subKey = snap.key
            val `val` = snap.getValue(String::class.java)
            if (subKey != null) {
                if (subKey == "Name") {
                    name = `val`
                } else if (subKey == "Type") {
                    type = `val`
                }
            }

        }
    }
}
