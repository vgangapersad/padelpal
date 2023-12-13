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
                val user = User(result.data.userId, result.data.username.toString(), result.data.username.toString(), result.data.profilePictureUrl.toString(), userPreferences)
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

    fun resetState() {
        _state.update { SignInState() }
    }
}