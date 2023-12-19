package edu.ap.padelpal.data.firestore

import android.annotation.SuppressLint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.models.MatchTypes
import edu.ap.padelpal.models.GenderPreferences
import edu.ap.padelpal.models.User
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime

class MatchRepository {
    val db = Firebase.firestore
    val collectionRef = db.collection("matches")
    val bookingRepository = BookingRepository()

    @SuppressLint("NewApi")
    suspend fun createMatch(
        clubId: String,
        date: LocalDate,
        startTime: LocalTime,
        durationMinutes: Int,
        title: String,
        players: List<User>,
        organizerId: String,
        amountOfPlayers: Int,
        matchType: MatchTypes,
        isPrivate: Boolean,
        genderPreference: GenderPreferences,
    ): String {
        try {
            val dateLong = date.toEpochDay()
            val timeLong = startTime.toSecondOfDay().toLong()
            val playerIds = mutableListOf<String>()

            players.forEach { p ->
                if(p.id.isNotBlank()){
                    playerIds.add(p.id)
                }
            }

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
                    userId = organizerId,
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

    suspend fun getAllMatches(isUpcoming: Boolean = false, isPast: Boolean = false) {

    }
}