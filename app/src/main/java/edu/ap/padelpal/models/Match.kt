package edu.ap.padelpal.models

data class Match(
    var id: String = "",
    val title: String = "",
    val date: Long = 0,
    val playerIds: List<String> = emptyList(),
    val organizerId: String = "",
    val amountOfPlayers: Int = 0,
    val matchType: MatchTypes = MatchTypes.friendly,
    var isPrivate: Boolean = false,
    val genderPreference: GenderPreferences = GenderPreferences.all
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