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

class MatchUtils {
    val matchRepository = MatchRepository()
    val clubRepository = ClubRepository()
    val bookingRepository = BookingRepository()

    @RequiresApi(Build.VERSION_CODES.O)
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

    suspend fun addPlayerByMatchId(matchId: String, playerId: String) {
        val match = matchRepository.getMatchById(matchId)

        if (match != null && match.amountOfPlayers > match.playerIds.size) {
            val updatedPlayerIds = match.playerIds.toMutableList()
            updatedPlayerIds.add(playerId)

            try {
                matchRepository.updateMatchPlayersById(matchId, updatedPlayerIds)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun removePlayerByMatchId(matchId: String, playerId: String) {
        val match = matchRepository.getMatchById(matchId)

        if (match != null && match.organizerId != playerId) {
            val updatedPlayerIds = match.playerIds.toMutableList()
            updatedPlayerIds.remove(playerId)

            try {
                matchRepository.updateMatchPlayersById(matchId, updatedPlayerIds)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun deleteMatchAndBooking(match: MatchDetailsResponse, userId: String){
       val foundMatch = getMatchWithDetails(match.match.id)
        if (foundMatch != null) {
            if (foundMatch.equals(match)) {
                if (match.match.organizerId == userId) {
                    try {
                        bookingRepository.deleteBookingById(match.booking.id)
                        matchRepository.deleteMatchById(match.match.id, userId)
                    } catch (e: Exception) {
                        throw e
                    }
                }
            }
        }
    }
}
