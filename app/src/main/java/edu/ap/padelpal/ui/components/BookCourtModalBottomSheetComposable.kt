package edu.ap.padelpal.ui.components

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.ap.padelpal.data.firestore.BookingRepository
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.models.StartTime
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.utilities.formatDateForDisplay
import edu.ap.padelpal.utilities.getAvailableStartTimes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookCourtModalBottomSheet(
    onShowBottomSheet: (Boolean) -> Unit,
    sheetState: SheetState,
    scope: CoroutineScope,
    isFocused: Boolean,
    onIsFocused: (Boolean) -> Unit,
    selectedClub: Club,
    onSelectedClub: (Club) -> Unit,
    clubs : List<Club>,
    selectedDate: LocalDate,
    onSelectedDate: (LocalDate) -> Unit,
    selectedTime: LocalTime?,
    onSelectedTime: (LocalTime) -> Unit
) {
    val bookingRepository = BookingRepository()
    var startTimes by remember { mutableStateOf(emptyList<StartTime>()) }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = bookingRepository, key2 = selectedDate) {
        startTimes = getAvailableStartTimes(selectedClub, selectedDate)
    }

    ModalBottomSheet(
        onDismissRequest = {
            onShowBottomSheet(false)
        },
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier.padding(PaddingValues(horizontal = 15.dp, vertical = 10.dp))
        ) {
            item {
                Text(
                    text = "Book a court",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(30.dp))
                var clubExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = clubExpanded,
                    onExpandedChange = { clubExpanded = !clubExpanded },
                    modifier = Modifier
                        .border(
                            1.dp,
                            if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .onFocusChanged { focusState -> onIsFocused(focusState.isFocused) }
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(4.dp)
                            ),
                        readOnly = true,
                        value = "${selectedClub.name} ${(selectedClub.location?.city)}",
                        onValueChange = {},
                        label = { Text("Club") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clubExpanded) },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = clubExpanded,
                        onDismissRequest = { clubExpanded = false },
                    ) {
                        clubs.forEach { club ->
                            DropdownMenuItem(
                                text = { Text("${club.name} (${club.location.city})") },
                                onClick = {
                                    onSelectedClub(club)
                                    clubExpanded = false
                                },
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(contentAlignment = Alignment.TopStart) {
                    FlowRow(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = { showDatePicker = true }) {
                            Text(text = formatDateForDisplay(selectedDate))
                        }
                        OutlinedButton(onClick = {}) {
                            Text(
                                text = if (selectedTime != null) "${selectedTime} - ${
                                    selectedTime.plusMinutes(
                                        90
                                    )
                                }" else "always 90 min"
                            )
                        }
                        FilledIconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Outlined.DateRange,
                                contentDescription = "Open calendar button"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (showDatePicker) {
                    CourtDatePickerDialog(
                        onDateSelected = { onSelectedDate(it) },
                        onDismiss = {
                            showDatePicker = false
                        }
                    )
                }
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    startTimes.forEach { startTime ->
                        FilledTonalButton(
                            onClick = { onSelectedTime(startTime.time) },
                            colors = if (selectedTime == startTime.time) ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ) else ButtonDefaults.filledTonalButtonColors(),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = startTime.label,
                                color = if (selectedTime == startTime.time) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onShowBottomSheet(false)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    enabled = selectedTime != null
                )
                {
                    Text("Select these options")
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}