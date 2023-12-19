package edu.ap.padelpal.models

data class Match(
    val id: String,
    val title: String,
    val date: Long,
    val playerIds: List<String>,
    val organizerId: String,
    val amountOfPlayers: Int,
    val matchType: MatchTypes,
    val isPrivate: Boolean,
    val genderPreference: GenderPreferences,
)

enum class MatchTypes {
    competitive,
    friendly,
}

enum class GenderPreferences {
    all,
    men,
    women,
}