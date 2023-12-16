package edu.ap.padelpal.data.firestore

import android.annotation.SuppressLint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.models.Booking
import edu.ap.padelpal.models.Club
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime

class BookingRepository {
    val db = Firebase.firestore
    val collectionRef = db.collection("bookings")

    suspend fun getAllBookingsByClub(clubId: String): List<Booking> {
        val bookings = mutableListOf<Booking>()
        try {
            val querySnapshot = collectionRef.whereEqualTo("clubId", clubId).get().await()
            if(!querySnapshot.isEmpty){
                for (doc in querySnapshot) {
                    val booking = doc.toObject(Booking::class.java)
                    // Set the auto-generated document ID as the 'id' property
                    booking.id = doc.id
                    bookings.add(booking)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return bookings
    }

    @SuppressLint("NewApi")
    suspend fun getAllBookingsByClubAndDate(clubId: String, date: LocalDate): List<Booking> {
        val bookings = mutableListOf<Booking>()
        try {
            val querySnapshot = collectionRef.whereEqualTo("clubId", clubId).whereEqualTo("date", date.toEpochDay()).get().await()
            if(!querySnapshot.isEmpty){
                for (doc in querySnapshot) {
                    val booking = doc.toObject(Booking::class.java)
                    // Set the auto-generated document ID as the 'id' property
                    booking.id = doc.id
                    bookings.add(booking)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return bookings
    }

    @SuppressLint("NewApi")
    suspend fun createBooking(clubId: String, userId: String, date: LocalDate, startTime: LocalTime, durationMinutes: Int): String {
        try {
            val dateLong = date.toEpochDay()
            val timeLong = startTime.toSecondOfDay().toLong()

            val bookingMap = mapOf(
                "clubId" to clubId,
                "matchId" to "fakeMatchId123",
                "userId" to userId,
                "date" to dateLong,
                "startTime" to timeLong,
                "durationMinutes" to durationMinutes
            )
            val documentRef = collectionRef.add(bookingMap).await()

            return documentRef.id

        } catch (e: Exception) {
            throw e
        }
    }
}