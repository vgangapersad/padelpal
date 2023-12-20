package edu.ap.padelpal.ui.matches

import InformationCard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ap.padelpal.data.firestore.UserRepository
import edu.ap.padelpal.models.MatchDetailsResponse
import edu.ap.padelpal.models.User
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.utilities.MatchUtils
import edu.ap.padelpal.utilities.formatDateForDisplay
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import edu.ap.padelpal.data.firestore.MatchRepository
import edu.ap.padelpal.models.Club
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(userData: UserData?, navController: NavController, matchId: String) {
    val matchUtils = MatchUtils()
    val userRepository = UserRepository()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    var match by remember { mutableStateOf<MatchDetailsResponse?>(null) }
    var friends by remember { mutableStateOf(emptyList<User>()) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<User?>(null) }

    val isOrganizer = match?.match?.organizerId == userData?.userId

    LaunchedEffect(key1 = matchUtils, key2 = userRepository, key3 = userRepository) {
        match = matchUtils.getMatchWithDetails(matchId)
        friends = userRepository.getAllUsers()
        user = match?.match?.organizerId?.let { userRepository.getUserFromFirestore(it) }
    }

    val selectedFriends: List<User> = friends.filter { user ->
        user.id?.let { match?.match?.playerIds?.contains(it) } ?: false
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = { Text("Match Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isOrganizer) {
                        IconButton(onClick = { showDeleteDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        }
                        if (showDeleteDialog.value) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog.value = false },
                                title = { Text("Delete Match") },
                                text = { Text("Are you sure you want to delete this match?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            scope.launch{
                                                userData?.userId?.let { match?.let { it1 -> matchUtils.deleteMatchAndBooking(match = it1, userId = it) } }
                                            }
                                            showDeleteDialog.value = false
                                            navController.popBackStack()
                                        }
                                    ) {
                                        Text("Yes")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog.value = false }) {
                                        Text("No")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
            LazyColumn( modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)) {

                item {
                    match?.let { match ->
                        Text(
                            text = match.match.title,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            InformationCard("${match.match.playerIds.size}/${match.match.amountOfPlayers}")
                            Spacer(modifier = Modifier.width(8.dp))
                            InformationCard(match.match.matchType.name.capitalize(Locale("EN")))
                            Spacer(modifier = Modifier.width(8.dp))
                            InformationCard(match.match.genderPreference.name.capitalize(Locale("EN")))
                            if (match.match.isPrivate){
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(modifier = Modifier
                                    .clip(RoundedCornerShape(20))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "organizer",
                                        tint = MaterialTheme.colorScheme.background,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(25.dp))

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
                                model = match.club.imageUrl,
                                contentDescription = match.club.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                text = match.club.name,
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
                    }
                }
                item {
                    match?.let { match ->
                        Spacer(modifier = Modifier.height(25.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "organizer",
                                tint =  MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            user?.displayName.let {
                                if (it != null) {
                                    Text(
                                        text = "$it (organizer)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(7.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val startTime = LocalTime.ofSecondOfDay(match.booking.startTime)
                            val timeslot =
                                "${startTime} - ${startTime.plusMinutes(match.booking.durationMinutes.toLong())}"
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Date",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = "${formatDateForDisplay(LocalDate.ofEpochDay(match.booking.date))}  $timeslot",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(7.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = match.club.location.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                item {
                    match?.let { match ->

                        Spacer(modifier = Modifier.height(25.dp))

                        Row(
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (i in 0 until match.match.amountOfPlayers) {
                                PlayerCircle(
                                    friend = selectedFriends.getOrNull(i)
                                )
                            }
                        }
                    }
                }

                item{
                    Spacer(modifier = Modifier.height(25.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .height(48.dp)
                            .fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Join",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(110.dp))
                }
            }
    }
}




@Composable
fun PlayerCircle(
    friend: User?
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (friend == null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            if (friend != null) {
                AsyncImage(
                    model = friend.profilePictureUrl,
                    contentDescription = friend.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp)
                )
                Box(
                    Modifier
                        .matchParentSize()
                        .clickable { }
                        .background(Color.Transparent)
                )
            } else {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add friend",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        friend?.displayName?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
                modifier = Modifier.width(60.dp)
            )
        }
    }
}