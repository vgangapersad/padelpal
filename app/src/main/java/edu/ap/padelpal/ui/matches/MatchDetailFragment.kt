package edu.ap.padelpal.ui.matches

import android.os.Build
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import edu.ap.padelpal.data.firestore.MatchRepository
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.ui.components.IndeterminateCircularIndicator
import edu.ap.padelpal.ui.components.InformationChip
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(userData: UserData?, navController: NavController, matchId: String) {
    val matchUtils = MatchUtils()
    val userRepository = UserRepository()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var match by remember { mutableStateOf<MatchDetailsResponse?>(null) }
    var friends by remember { mutableStateOf(emptyList<User>()) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<User?>(null) }

    val isOrganizer = match?.match?.organizerId == userData?.userId
    var isJoined: Boolean? by remember { mutableStateOf(null) }
    var joinedPlayers: Int? by remember { mutableStateOf(null) }
    var buttonText = "Join"

    if (isOrganizer) {
        buttonText = "Organized"
    } else {
        if (isJoined == true) {
            buttonText = "Leave"
        }
    }

    LaunchedEffect(key1 = matchUtils, key2 = userRepository, key3 = userRepository) {
        match = matchUtils.getMatchWithDetails(matchId)
        if (match != null) {
            if (userData != null) {
                isJoined = match!!.match.playerIds.contains(userData.userId)
                user = match!!.match.organizerId.let { userRepository.getUserFromFirestore(it) }
            }
            joinedPlayers = match!!.match.playerIds.size
        }
        friends = userRepository.getAllUsers()
    }

    var selectedFriends: MutableList<User> = friends.filter { user ->
        user.id?.let { match?.match?.playerIds?.contains(it) } ?: false
    }.toMutableList()


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
                title = { Text("Match details") },
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
                                title = { Text("Delete ${match?.match?.title}") },
                                text = { Text("Are you sure you want to delete this match?") },
                                modifier = Modifier.fillMaxWidth(),
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
                                    Button(onClick = { showDeleteDialog.value = false }) {
                                        Text("Cancel")
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
                if (match != null && user != null && userData != null && selectedFriends.isNotEmpty()) {
                    item {
                        match?.let { match ->
                            Text(
                                text = match.match.title,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                InformationChip(
                                    "${match.match.playerIds.size}/${match.match.amountOfPlayers}",
                                    MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                InformationChip(
                                    match.match.matchType.name.capitalize(Locale("EN")),
                                    MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                InformationChip(
                                    match.match.genderPreference.name.capitalize(
                                        Locale(
                                            "EN"
                                        )
                                    ), MaterialTheme.colorScheme.secondary
                                )
                                if (match.match.isPrivate) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20))
                                            .background(MaterialTheme.colorScheme.secondary)
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = "organizer",
                                            tint = MaterialTheme.colorScheme.onSecondary,
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
                                    tint = MaterialTheme.colorScheme.onBackground,
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
                                horizontalArrangement = if (match.match.amountOfPlayers == 4) Arrangement.SpaceBetween else Arrangement.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                            ) {
                                for (i in 0 until match.match.amountOfPlayers) {
                                    DetailPlayerCircle(
                                        friend = selectedFriends.getOrNull(i),
                                        amountOfPlayers = match.match.amountOfPlayers
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(25.dp))
                        if (!isOrganizer && isJoined != null && joinedPlayers != null) {
                            Button(
                                onClick = {
                                    if (isJoined == true) {
                                        val result = scope.launch {
                                            match?.match?.let {
                                                if (userData != null) {
                                                    matchUtils.removePlayerByMatchId(
                                                        it.id,
                                                        userData.userId
                                                    )
                                                }
                                            }
                                        }
                                        if (!result.isCancelled) {
                                            isJoined = false
                                            joinedPlayers = joinedPlayers!! - 1
                                            selectedFriends.remove(user)
                                            Toast.makeText(
                                                context,
                                                "You left ${match?.match?.title}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Try again later",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }
                                    } else {
                                        val result = scope.launch {
                                            match?.match?.let {
                                                if (userData != null) {
                                                    matchUtils.addPlayerByMatchId(
                                                        it.id,
                                                        userData.userId
                                                    )
                                                }
                                            }
                                        }
                                        if (!result.isCancelled) {
                                            isJoined = true
                                            joinedPlayers = joinedPlayers!! + 1
                                            user?.let { selectedFriends.add(it) }
                                            Toast.makeText(
                                                context,
                                                "You joined ${match?.match?.title}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Try again later",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .height(48.dp)
                                    .fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = buttonText,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(110.dp))
                    }
                } else {
                    item {
                        IndeterminateCircularIndicator(label = "Getting the details")
                    }
                }
            }
    }
}




@Composable
fun DetailPlayerCircle(
    friend: User?,
    amountOfPlayers: Int
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = if (amountOfPlayers == 4) Modifier.padding(8.dp) else Modifier.padding((16.dp))
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
                    Icons.Filled.AccountCircle,
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