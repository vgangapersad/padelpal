package edu.ap.padelpal.utilities

import android.os.Build
import androidx.annotation.RequiresApi
import edu.ap.padelpal.data.firestore.BookingRepository
import edu.ap.padelpal.data.firestore.ClubRepository
import edu.ap.padelpal.data.firestore.MatchRepository
import edu.ap.padelpal.models.Booking
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.models.MatchDetailsResponse
import java.time.LocalDate

class DisplayMatchUtils {
    val matchRepository = MatchRepository()
    val clubRepository = ClubRepository()
    val bookingRepository = BookingRepository()

    suspend fun getMatchesWithDetails(
        isUpcoming: Boolean = false,
        isPast: Boolean = false
    ): List<MatchDetailsResponse> {
        val matches = matchRepository.getAllMatches(isUpcoming, isPast)
        return matches.map { match ->
            val booking = bookingRepository.getBookingByMatchId(match.id ?: "")
            val club = clubRepository.getClub(booking?.clubId ?: "")

            MatchDetailsResponse(match, booking ?: Booking(), club)
        }
    }


    suspend fun getMatchWithDetails(matchId: String): MatchDetailsResponse? {
        val match = matchRepository.getMatchById(matchId) ?: return null
        val booking = bookingRepository.getBookingByMatchId(matchId) ?: return null
        val club = clubRepository.getClub(booking.clubId)
        return MatchDetailsResponse(match, booking, club)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMatchesWithDetailsByDate(
        targetDate: LocalDate,
    ): List<MatchDetailsResponse> {
        val matches = matchRepository.getMatchesByDate(targetDate)
        return matches.map { match ->
            val booking = bookingRepository.getBookingByMatchId(match.id ?: "")
            val club = clubRepository.getClub(booking?.clubId ?: "")

            MatchDetailsResponse(match, booking ?: Booking(), club ?: Club())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMatchesWithDetailsByClub(
        clubId: String,
        isUpcoming: Boolean = false,
        isPast: Boolean = false
    ): List<MatchDetailsResponse> {
        val matches = matchRepository.getMatchesByClub(clubId, isUpcoming, isPast)
        return matches.map { match ->
            val booking = bookingRepository.getBookingByMatchId(match.id ?: "")
            val club = clubRepository.getClub(booking?.clubId ?: "")

            MatchDetailsResponse(match, booking ?: Booking(), club)
        }
    }

    suspend fun getMatchesWithDetailsByOrganizer(
        userId: String,
    ): List<MatchDetailsResponse> {
        val matches = matchRepository.getMatchesByOrganizer(userId)
        return matches.map { match ->
            val booking = bookingRepository.getBookingByMatchId(match.id ?: "")
            val club = clubRepository.getClub(booking?.clubId ?: "")

            MatchDetailsResponse(match, booking ?: Booking(), club)
        }
    }

}