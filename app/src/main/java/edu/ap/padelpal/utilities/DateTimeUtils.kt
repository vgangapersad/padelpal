package edu.ap.padelpal.utilities

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import edu.ap.padelpal.data.firestore.BookingRepository
import edu.ap.padelpal.models.Booking
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.models.StartTime
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

var bookingRepository = BookingRepository()

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getAvailableStartTimes(club: Club, date: LocalDate): List<StartTime> {
    val bookings = bookingRepository.getAllBookingsByClubAndDate(club.id, date)
    val openingTime = LocalTime.of(club.openingHours.startTime, 0, 0)
    val closingTime = LocalTime.of(club.openingHours.endTime, 0, 0)
    val availableStartTimes = mutableListOf<StartTime>()

    var currentDateTime = LocalDateTime.of(date, openingTime)
    val halfHourDuration = Duration.ofMinutes(30)

    while (currentDateTime.toLocalTime() <= closingTime.minusMinutes(90)) {
        val currentTime = currentDateTime.toLocalTime()
        val label = currentTime.format(DateTimeFormatter.ofPattern("H:mm"))
        val startTime = StartTime(label, currentTime)

            if (!(bookings.any { LocalTime.ofSecondOfDay(it.startTime) == currentTime })){
                if(!(bookings.any {LocalTime.ofSecondOfDay(it.startTime).plusMinutes(30) == currentTime})){
                    if (!(bookings.any {LocalTime.ofSecondOfDay(it.startTime).plusMinutes(60) == currentTime})){
                        if (!(bookings.any {LocalTime.ofSecondOfDay(it.startTime).minusMinutes(30) == currentTime})){
                            if (!(bookings.any {LocalTime.ofSecondOfDay(it.startTime).minusMinutes(60) == currentTime})){
                                availableStartTimes.add(startTime)
                            }
                        }
                    }
                }
            }

        currentDateTime = currentDateTime.plus(halfHourDuration)
    }

    return availableStartTimes.toList()
}

@SuppressLint("NewApi")
fun formatDateForDisplay(date: LocalDate): String {
    val currentYear = LocalDate.now().year

    val pattern = if (date.year == currentYear) {
        "EEE d MMM"
    } else {
        "EEE d MMM yyyy"
    }

    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    return date.format(formatter)
}