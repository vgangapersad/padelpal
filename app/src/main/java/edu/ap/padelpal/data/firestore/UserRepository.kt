package edu.ap.padelpal.data.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.models.CourtPositionPreference
import edu.ap.padelpal.models.HandPreference
import edu.ap.padelpal.models.LocationPreference
import edu.ap.padelpal.models.MatchTypePreference
import edu.ap.padelpal.models.Preferences
import edu.ap.padelpal.models.TimePreference
import edu.ap.padelpal.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {
    val db = Firebase.firestore
    val collectionRef = db.collection("users");

    suspend fun getUserFromFirestore(uid: String): User? {
        return try {
            val userDocument = collectionRef.document(uid).get().await()
            if (userDocument.exists()) {
                val preferencesMap = userDocument.get("preferences") as? Map<String, String>
                val preferences = Preferences(
                    location = LocationPreference.valueOf(preferencesMap?.get("location") ?: "notSet"),
                    bestHand = HandPreference.valueOf(preferencesMap?.get("bestHand") ?: "notSet"),
                    courtPosition = CourtPositionPreference.valueOf(preferencesMap?.get("courtPosition") ?: "notSet"),
                    matchType = MatchTypePreference.valueOf(preferencesMap?.get("matchType") ?: "notSet"),
                    preferredTime = TimePreference.valueOf(preferencesMap?.get("preferredTime") ?: "notSet")
                )

                User(
                    id = userDocument.getString("id") ?: "",
                    username = userDocument.getString("username") ?: "",
                    displayName = userDocument.getString("displayName") ?: "",
                    profilePictureUrl = userDocument.getString("profilePictureUrl") ?: "",
                    preferences = preferences,
                    matchesWon = userDocument.getLong("matchesWon")?.toInt() ?: 0,
                    matchesPlayed = userDocument.getLong("matchesPlayed")?.toInt() ?: 0,
                    level = userDocument.getLong("level")?.toInt() ?: 1
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val users = mutableListOf<User>()

            val usersSnapshot = collectionRef.get().await()
            for (userDocument in usersSnapshot) {
                val preferencesMap = userDocument.get("preferences") as? Map<String, String>
                val preferences = Preferences(
                    location = LocationPreference.valueOf(preferencesMap?.get("location") ?: "notSet"),
                    bestHand = HandPreference.valueOf(preferencesMap?.get("bestHand") ?: "notSet"),
                    courtPosition = CourtPositionPreference.valueOf(preferencesMap?.get("courtPosition") ?: "notSet"),
                    matchType = MatchTypePreference.valueOf(preferencesMap?.get("matchType") ?: "notSet"),
                    preferredTime = TimePreference.valueOf(preferencesMap?.get("preferredTime") ?: "notSet")
                )

                val user = User(
                    id = userDocument.getString("id") ?: "",
                    username = userDocument.getString("username") ?: "",
                    displayName = userDocument.getString("displayName") ?: "",
                    profilePictureUrl = userDocument.getString("profilePictureUrl") ?: "",
                    preferences = preferences,
                    matchesWon = userDocument.getLong("matchesWon")?.toInt() ?: 0,
                    matchesPlayed = userDocument.getLong("matchesPlayed")?.toInt() ?: 0,
                    level = userDocument.getLong("level")?.toInt() ?: 1
                )

                users.add(user)
            }

            users
        } catch (e: Exception) {
            throw e
        }
    }
}