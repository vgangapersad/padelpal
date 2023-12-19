package edu.ap.padelpal.data.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.models.Club
import kotlinx.coroutines.tasks.await

class ClubRepository {
    val db = Firebase.firestore
    val collectionRef = db.collection("clubs")

    suspend fun getAllClubs(): List<Club> {
        val clubs = mutableListOf<Club>()
        try {
            val querySnapshot = collectionRef.get().await()
            for (doc in querySnapshot) {
                val club = doc.toObject(Club::class.java)
                // Set the auto-generated document ID as the 'id' property
                club.id = doc.id
                clubs.add(club)
            }
        } catch (e: Exception) {
            throw e
        }
        return clubs
    }

    suspend fun getClub(id: String): Club {
        val club: Club
        try {
            val querySnapshot = collectionRef.document(id).get().await()
                club = querySnapshot.toObject(Club::class.java)!!
                // Set the auto-generated document ID as the 'id' property
                club.id = querySnapshot.id
            } catch (e: Exception) {
            throw e
        }
        return club
    }
}