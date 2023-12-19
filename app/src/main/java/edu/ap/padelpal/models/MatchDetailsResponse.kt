package edu.ap.padelpal.models

data class MatchDetailsResponse(
    val match: Match,
    val booking: Booking,
    val club: Club,
)
