package edu.ap.padelpal.models

data class MatchCardResponse(
    val match: Match,
    val booking: Booking,
    val club: Club,
)
