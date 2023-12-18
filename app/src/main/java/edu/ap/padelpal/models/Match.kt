package edu.ap.padelpal.models

data class Match(
    val id: String,
    val title: String,
    val date: Long,
    val playerIds: List<String>,
    val organizerId: String,
    val amountOfPlayers: Int,
    val matchType: MatchType,
    val isPrivate: Boolean,
    val genderPreference: genderPreference,
)

enum class MatchType {
    competitive,
    friendly,
}

enum class genderPreference {
    all,
    men,
    women,
}