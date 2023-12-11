package edu.ap.padelpal.models

data class Club(
    var id: String = "", // PadelPal uses the auto-generated document ID from Firestore.
    val name: String = "",
    val location: Location = Location(),
    val imageUrl: String = "",
    val openingHours: OpeningHours = OpeningHours()
)

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String = "",
    val city: String = "",
    val address: String = ""
)

data class OpeningHours(
    val startTime: Int = 0,
    val endTime: Int = 0
)