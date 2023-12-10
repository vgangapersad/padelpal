package edu.ap.padelpal.presentation.sign_in

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.models.Location
import edu.ap.padelpal.models.OpeningHours
import edu.ap.padelpal.models.Preferences
import edu.ap.padelpal.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    val db = Firebase.firestore

    suspend fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        )}
        if(result.data != null){
            if (!checkIfUserExists(result.data.userId)){
                val userPreferences = Preferences()
                val user = User(result.data.userId, result.data.username.toString(), result.data.username.toString(), userPreferences)
                db.collection("users").document(result.data.userId)
                    .set(user)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            }
        }
    }

    private suspend fun checkIfUserExists(userId : String): Boolean {
        val documentRef = db.collection("users").document(userId)
        return try {
            val documentSnapshot = documentRef.get().await()
            documentSnapshot.exists()
        } catch (e: Exception) {
            false
        }

    }

    private fun generateRandomClub(): Club {
        val random = java.util.Random()

        val clubNames = listOf(
            "Padel Pro Club",
            "Urban Padel",
            "Ace Padel Center",
            "Epic Padel Zone",
            "Green Court Padel",
            "Top Spin Padel",
            "Swift Padel Arena",
            "Elite Padel Lounge",
            "Dynamic Smash Club",
            "Perfect Padel Haven"
        )

        val locations = listOf(
            Location(51.2194, 4.4025, "Belgium", "Antwerp", "Antwerp Street 123"),
            Location(50.8503, 4.3517, "Belgium", "Brussels", "Brussels Avenue 456"),
            Location(51.0543, 3.7174, "Belgium", "Gent", "Gent Lane 789")
        )

        val imageUrls = listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg",
        )

        // Randomly select values
        val randomClubName = clubNames.random()
        val randomLocation = locations.random()
        val randomImageUrl = imageUrls.random()

        val openingHours = OpeningHours(startTime = 9, endTime = 21)

        return Club(
            id = "",
            name = randomClubName,
            location = randomLocation,
            imageUrl = randomImageUrl,
            openingHours = openingHours
        )
    }


    private fun generateRandomClubs(): List<Club> {
        val clubs = mutableListOf<Club>()
        repeat(10) {
            clubs.add(generateRandomClub())
        }
        return clubs
    }

    suspend fun saveGeneratedClubs() {
        val generatedClubs = generateRandomClubs()
        for (club in generatedClubs) {
            db.collection("clubs").document(club.id)
                .set(club)
                .addOnSuccessListener { Log.d(TAG, "Club successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing club document", e) }
        }
    }
    fun resetState() {
        _state.update { SignInState() }
    }
}