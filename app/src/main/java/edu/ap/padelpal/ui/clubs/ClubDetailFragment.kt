import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ap.padelpal.data.firestore.BookingRepository
import edu.ap.padelpal.data.firestore.ClubRepository
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.models.StartTime
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.ui.components.CourtDatePickerDialog
import edu.ap.padelpal.utilities.formatDateForDisplay
import java.time.LocalDate
import edu.ap.padelpal.utilities.getAvailableStartTimes
import kotlinx.coroutines.launch
import java.time.LocalTime

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)
@Composable
fun ClubDetailScreen(userData: UserData?, navController: NavController, clubId: String) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val clubRepository = ClubRepository()
    val bookingRepository = BookingRepository()

    var club by remember { mutableStateOf(Club()) }
    var startTimes by remember { mutableStateOf(emptyList<StartTime>()) }
    var selectedDate by remember {
        mutableStateOf<LocalDate>(LocalDate.now())
    }
    var selectedTime by remember {
        mutableStateOf<LocalTime?>(null)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = clubRepository, key2 = selectedDate) {
        club = clubRepository.getClub(clubId)
        startTimes = getAvailableStartTimes(club, selectedDate)
    }

    LaunchedEffect(key1 = selectedDate, key2 = startTimes) {
        startTimes = getAvailableStartTimes(club, selectedDate)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            club.let {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    title = { Text(it.name) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        club.let {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.background,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        AsyncImage(
                            model = it.imageUrl,
                            contentDescription = club.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = "Mo - Sa from ${club.openingHours.startTime}:00 to ${club.openingHours.endTime}:00",
                            modifier = Modifier
                                .padding(16.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .align(Alignment.BottomStart)
                                .padding(8.dp),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "${it.location.address}, ${it.location.city}",
                            style = MaterialTheme.typography.bodyLarge
                        )

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
                                    text = if (selectedTime != null) "${selectedTime.toString()} - ${
                                        selectedTime!!.plusMinutes(
                                            90
                                        ).toString()
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
                            onDateSelected = { selectedDate = it },
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
                                onClick = { selectedTime = startTime.time },
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
                            if (userData != null && selectedTime != null) {
                                val bookingResult =  coroutineScope.launch {
                                    bookingRepository.createBooking(
                                        clubId = clubId,
                                        userId = userData.userId,
                                        date = selectedDate,
                                        startTime = selectedTime!!,
                                        durationMinutes = 90
                                    )
                                }
// TO DO: Make the snackbar work so no toast is needed.
//                                coroutineScope.launch {
                                    if (!bookingResult.isCancelled){
                                        Toast.makeText(context, "Booking successful!", Toast.LENGTH_LONG).show()
                                        coroutineScope.launch {
                                            startTimes = getAvailableStartTimes(club, selectedDate)
                                        }
//                                        snackbarHostState.showSnackbar("Booking successful!")
                                    } else {
//                                        snackbarHostState.showSnackbar("Try again later")
                                        Toast.makeText(context, "Try again later", Toast.LENGTH_LONG).show()
                                    }
                                }
//                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        enabled = selectedTime != null
                    )
                    {
                        Text("Book this court")
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}


