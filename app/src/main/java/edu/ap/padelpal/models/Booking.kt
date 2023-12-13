package edu.ap.padelpal.models

import java.time.LocalDate
import java.time.LocalTime

data class Booking(
    var id: String = "",
    val clubId: String = "",
    val matchId: String = "",
    val userId: String = "",
    val date: Long = 0,
    val startTime: Long = 0,
    val durationMinutes: Int = 0
)
