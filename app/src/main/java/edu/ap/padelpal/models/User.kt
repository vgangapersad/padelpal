package edu.ap.padelpal.models

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val preferences: Preferences,
    val matchesWon: Int = 0,
    val matchesPlayed: Int = 0,
    val level: Int =  1,
)


data class Preferences(
    val location: LocationPreference = LocationPreference.notSet,
    val bestHand: HandPreference = HandPreference.notSet,
    val courtPosition: CourtPositionPreference = CourtPositionPreference.notSet,
    val matchType: MatchTypePreference = MatchTypePreference.notSet,
    val preferredTime: TimePreference = TimePreference.notSet,
)

enum class LocationPreference {
    antwerpen,
    gent,
    brussel,
    mechelen,
    notSet
}

enum class HandPreference {
    rightHanded,
    leftHanded,
    bothHands,
    notSet
}

enum class CourtPositionPreference {
    bothSides,
    backhand,
    forehand,
    notSet
}

enum class MatchTypePreference {
    competitive,
    friendly,
    both,
    notSet
}

enum class TimePreference {
    morning,
    afternoon,
    evening,
    allDay,
    notSet
}