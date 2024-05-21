package celo.urestaurants.models

import com.google.firebase.database.DataSnapshot

class CatConfig {
    var sections: MutableList<SectionModel> = mutableListOf()

    fun loadSectionsData(snapshot: DataSnapshot) {
        sections = mutableListOf()
        for (snap in snapshot.children) {
            for (snapData in snap.children) {
                val section = SectionModel()
                section.loadSection(snapData)
                sections.add(section)
            }
        }
    }
}
