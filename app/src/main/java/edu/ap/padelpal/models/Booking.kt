package edu.ap.padelpal.models

import java.util.Date

data class Booking(
    val id: String,
    val clubId: String,
    val matchId: String,
    val userId: String,
    val startTime: Date,
    val durationMinutes: Int
)
