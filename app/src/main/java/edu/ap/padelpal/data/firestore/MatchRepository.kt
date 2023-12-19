package edu.ap.padelpal.data.firestore

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.models.MatchTypes
import edu.ap.padelpal.models.GenderPreferences
import edu.ap.padelpal.models.Match
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllMatches(isUpcoming: Boolean = false, isPast: Boolean = false): List<Match> {
        val matches = mutableListOf<Match>()
        try {
            val currentTimestamp = LocalDate.now().toEpochDay()

            val query = when {
                isUpcoming -> {
                    collectionRef
                        .whereGreaterThanOrEqualTo("date", currentTimestamp)
                        .orderBy("date")
                }
                isPast -> {
                    collectionRef
                        .whereLessThan("date", currentTimestamp)
                        .orderBy("date", Query.Direction.DESCENDING)
                }
                else -> {
                    collectionRef.orderBy("date", Query.Direction.DESCENDING)
                }
            }

            val querySnapshot = query.get().await()

            for (doc in querySnapshot) {
                val match = doc.toObject(Match::class.java)
                // Set the auto-generated document ID as the 'id' property
                match.id = doc.id
                matches.add(match)
            }
        } catch (e: Exception) {
            throw e
        }
        return matches
    }

    suspend fun getMatchById(matchId: String): Match? {
        try {
            val documentSnapshot = collectionRef.document(matchId).get().await()
            if (documentSnapshot.exists()) {
                val match = documentSnapshot.toObject(Match::class.java)
                // Set the auto-generated document ID as the 'id' property
                match?.id = documentSnapshot.id
                return match
            }
        } catch (e: Exception) {
            throw e
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMatchesByDate(targetDate: LocalDate): List<Match> {
        val matches = mutableListOf<Match>()
        try {
            val querySnapshot = collectionRef
                .whereEqualTo("date", targetDate.toEpochDay())
                .get()
                .await()

            for (doc in querySnapshot) {
                val match = doc.toObject(Match::class.java)
                // Set the auto-generated document ID as the 'id' property
                match.id = doc.id
                matches.add(match)
            }
        } catch (e: Exception) {
            throw e
        }
        return matches
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMatchesByClub(
        clubId: String,
        isUpcoming: Boolean = false,
        isPast: Boolean = false
    ): List<Match> {
        val matches = mutableListOf<Match>()
        try {
            val currentDate = LocalDate.now()
            val bookings = bookingRepository.getAllBookingsByClub(clubId)

            val matchingBookings = when {
                isUpcoming -> bookings.filter { it.date >= currentDate.toEpochDay() }
                isPast -> bookings.filter { it.date < currentDate.toEpochDay() }
                else -> bookings
            }

            val matchingMatchIds = matchingBookings.map { it.matchId }

            val querySnapshot = collectionRef
                .whereIn("id", matchingMatchIds)
                .orderBy("date")
                .get()
                .await()

            for (doc in querySnapshot) {
                val match = doc.toObject(Match::class.java)
                // Set the auto-generated document ID as the 'id' property
                match.id = doc.id
                matches.add(match)
            }
        } catch (e: Exception) {
            throw e
        }
        return matches
    }

    suspend fun getMatchesByOrganizer(userId: String): List<Match> {
        val matches = mutableListOf<Match>()
        try {
            val querySnapshot = collectionRef
                .whereEqualTo("organizerId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            for (doc in querySnapshot) {
                val match = doc.toObject(Match::class.java)
                // Set the auto-generated document ID as the 'id' property
                match.id = doc.id
                matches.add(match)
            }
        } catch (e: Exception) {
            throw e
        }
        return matches
    }
}