package edu.ap.padelpal.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                // Blocks past dates and Sundays from being selected.
                @RequiresApi(Build.VERSION_CODES.O)
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Get the current date at midnight
                    val currentDate = LocalDate.now(ZoneId.of("UTC"))

                    // Get the selected date
                    val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.of("UTC")).toLocalDate()

                    // Check if the selected date is today or in the future and not a Sunday
                    return (selectedDate.isEqual(currentDate) || selectedDate.isAfter(currentDate))
                            && selectedDate.dayOfWeek != DayOfWeek.SUNDAY
                }

                // Allow date selection up to 6 months from today
                @RequiresApi(Build.VERSION_CODES.O)
                override fun isSelectableYear(year: Int): Boolean {
                    return year <= LocalDate.now().plusMonths(6).year
                }
            }
        )

        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                Button(
                    modifier = Modifier.padding(PaddingValues(end = 10.dp, bottom = 10.dp)),
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                            onDateSelected(
                                convertDateMillisToLocalDate(selectedDateMillis)
                            )
                        }
                        onDismiss()
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("Check availability")
                }
            },
            dismissButton = {
                TextButton(
                    modifier = Modifier.padding(PaddingValues(bottom = 10.dp)),
                    onClick = {
                        onDismiss()
                    }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@SuppressLint("NewApi")
fun convertDateMillisToLocalDate(dateMillis: Long): LocalDate {
    val instant = Instant.ofEpochMilli(dateMillis)
    return instant.atZone(ZoneId.systemDefault()).toLocalDate()
}