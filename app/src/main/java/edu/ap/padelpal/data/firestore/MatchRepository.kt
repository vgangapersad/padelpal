package edu.ap.padelpal.data.firestore

import android.annotation.SuppressLint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.models.MatchTypes
import edu.ap.padelpal.models.GenderPreferences
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime

class MatchRepository {
    val db = Firebase.firestore
    val collectionRef = db.collection("matches")
    val bookingRepository = BookingRepository()

    @SuppressLint("NewApi")
    suspend fun createMatch(
        userId: String,
        clubId: String,
        date: LocalDate,
        startTime: LocalTime,
        durationMinutes: Int,
        title: String,
        playerIds: List<String>,
        organizerId: String,
        amountOfPlayers: Int,
        matchType: MatchTypes,
        isPrivate: Boolean,
        genderPreference: GenderPreferences,
    ): String {
        try {
            val dateLong = date.toEpochDay()
            val timeLong = startTime.toSecondOfDay().toLong()

            val matchMap = mapOf(
                "date" to dateLong,
                "startTime" to timeLong,
                "durationMinutes" to durationMinutes,
                "title" to title,
                "playerIds" to playerIds,
                "organizerId" to organizerId,
                "amountOfPlayers" to amountOfPlayers,
                "matchType" to matchType.name,
                "isPrivate" to isPrivate,
                "genderPreference" to genderPreference.name
            )

            val matchDocRef = collectionRef.add(matchMap).await()

            try{
                bookingRepository.createBooking(
                    clubId = clubId,
                    userId = userId,
                    matchId = matchDocRef.id,
                    date = date,
                    startTime = startTime,
                    durationMinutes = 90
                )
            } catch (e: Exception){
                throw e
            }

            return matchDocRef.id

        } catch (e: Exception) {
            throw e
        }
    }
}