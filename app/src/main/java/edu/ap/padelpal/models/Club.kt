package edu.ap.padelpal.models

data class Club(
    val id: String,
    val name: String,
    val location: Location,
    val imageUrl: String,
    val openingHours: OpeningHours
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val city: String,
    val address: String
)

data class OpeningHours(
    val startTime: Int,
    val endTime: Int
)