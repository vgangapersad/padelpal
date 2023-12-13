package edu.ap.padelpal.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
                // Blocks Sunday from being selected.
                @RequiresApi(Build.VERSION_CODES.O)
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val dayOfWeek = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC"))
                            .toLocalDate().dayOfWeek
                        dayOfWeek != DayOfWeek.SUNDAY
                    } else {
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        calendar.timeInMillis = utcTimeMillis
                        calendar[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY }
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
                TextButton(onClick = {
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