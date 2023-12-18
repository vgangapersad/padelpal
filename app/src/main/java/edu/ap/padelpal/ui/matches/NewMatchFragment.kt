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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ap.padelpal.data.firestore.BookingRepository
import edu.ap.padelpal.data.firestore.ClubRepository
import edu.ap.padelpal.data.firestore.MatchRepository
import edu.ap.padelpal.data.firestore.UserRepository
import edu.ap.padelpal.models.Booking
import edu.ap.padelpal.models.Club
import edu.ap.padelpal.models.GenderPreferences
import edu.ap.padelpal.models.MatchTypes
import edu.ap.padelpal.models.StartTime
import edu.ap.padelpal.models.User
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.ui.components.BookCourtModalBottomSheet
import edu.ap.padelpal.ui.components.CourtDatePickerDialog
import edu.ap.padelpal.ui.components.IndeterminateCircularIndicator
import edu.ap.padelpal.utilities.getAvailableStartTimes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMatchScreen(userData: UserData?, navController: NavController) {
    val matchRepository = MatchRepository()
    val bookingRepository = BookingRepository()
    val clubRepository = ClubRepository()
    val userRepository = UserRepository()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var clubs by remember { mutableStateOf(emptyList<Club>()) }
    var bookings by remember { mutableStateOf(emptyList<Booking>()) }

    val snackbarHostState = remember { SnackbarHostState() }
    var title by remember { mutableStateOf("") }
    var selectedClub by remember { mutableStateOf<Club?>(null) }
    var selectedGenderPreference by remember { mutableStateOf(GenderPreferences.all) }
    var isFocused by remember { mutableStateOf(false) }
    var friends by remember { mutableStateOf(emptyList<User>()) }
    var selectedTypeOfMatch by remember { mutableStateOf(MatchTypes.friendly) }
    var selectedMaxPlayers by remember { mutableStateOf(2) }
    var selectedFriends by remember { mutableStateOf<List<User>>(listOf()) }
    var showFriendSelectionDialog by remember { mutableStateOf(false) }
    var currentFriendToModify by remember { mutableStateOf<User?>(null) }
    var showModifyFriendDialog by remember { mutableStateOf(false) }
    var isPrivate by remember { mutableStateOf(false) }

    fun onAddFriend() {
        showFriendSelectionDialog = true
    }

    fun onModifyFriend(friend: User) {
        currentFriendToModify = friend
        showModifyFriendDialog = true
    }

    LaunchedEffect(key1 = clubs, key2 = friends) {
        clubs = clubRepository.getAllClubs()
        selectedClub = clubs[0]

        friends = userRepository.getAllUsers()
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
                title = { Text(text = "New Match") },
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            if (!(clubs.isEmpty() || friends.isEmpty())) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("An awesome match title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                    )

                    var genderExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded },
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
                                .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                                .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp)),
                            readOnly = true,
                            value = selectedGenderPreference.name.capitalize(),
                            onValueChange = {},
                            label = { Text("Gender preference") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false },
                        ) {
                            GenderPreferences.entries.forEach { preference ->
                                DropdownMenuItem(
                                    text = { Text(preference.name.capitalize()) },
                                    onClick = {
                                        selectedGenderPreference = preference
                                        genderExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(35.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CheckboxMaxPlayers(
                            selectedMaxPlayers = selectedMaxPlayers,
                            onSelectMaxPlayers = { newSelection ->
                                selectedMaxPlayers = newSelection
                            },
                            number1 = 2,
                            number2 = 4
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 0 until selectedMaxPlayers) {
                            PlayerCircle(
                                friend = selectedFriends.getOrNull(i),
                                onCircleClicked = { friend ->
                                    if (friend == null) {
                                        onAddFriend()
                                    } else {
                                        onModifyFriend(friend)
                                    }
                                },
                                onRemoveOrReplace = { friend: User ->
                                    onModifyFriend(friend)
                                }
                            )
                        }
                    }
                    if (showFriendSelectionDialog) {
                        FriendSelectionDialog(
                            friendList = friends.filter { it !in selectedFriends },
                            onFriendSelected = { selectedFriend ->
                                selectedFriends = selectedFriends + selectedFriend
                                showFriendSelectionDialog = false
                            },
                            onDismissRequest = {
                                showFriendSelectionDialog = false
                            }
                        )
                    }
                    if (showModifyFriendDialog && currentFriendToModify != null) {
                        ModifyFriendDialog(
                            friend = currentFriendToModify!!,
                            onRemove = {
                                selectedFriends =
                                    selectedFriends.filter { it != currentFriendToModify }
                                showModifyFriendDialog = false
                                currentFriendToModify = null
                            },
                            onDismissRequest = {
                                showModifyFriendDialog = false
                                currentFriendToModify = null
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(35.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Type of match: ",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.width(15.dp))

                        Checkbox(
                            selectedOption = selectedTypeOfMatch,
                            onOptionSelected = { selectedTypeOfMatch = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    PrivateMatchSwitch(
                        isChecked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                }
                item {
                    OutlinedButton(
                        onClick = {
                            showBottomSheet = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )
                    {
                        Text("Book a court")
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
                item {
                    if (showBottomSheet) {
                        selectedClub?.let {
                            BookCourtModalBottomSheet(
                                onShowBottomSheet = {s -> showBottomSheet = s},
                                sheetState = sheetState,
                                scope = scope,
                                isFocused = isFocused,
                                onIsFocused = {o -> isFocused = o},
                                selectedClub = it,
                                onSelectedClub = {o -> selectedClub = o},
                                clubs = clubs,
                                userData = userData,
                            )
                        }
                    }
                }
            } else {
                item {
                    IndeterminateCircularIndicator(label = "Loading your best options")
                }
            }
        }
    }
}

@Composable
fun CheckboxMaxPlayers(
    selectedMaxPlayers: Int,
    onSelectMaxPlayers: (Int) -> Unit,
    number1: Int,
    number2: Int
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Button(
            onClick = { onSelectMaxPlayers(number1) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMaxPlayers == number1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
        ) {
            Text("$number1 players", fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { onSelectMaxPlayers(number2) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMaxPlayers == number2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
        ) {
            Text("$number2 players", fontSize = 12.sp)
        }
    }
}

@Composable
fun PlayerCircle(
    friend: User?,
    onCircleClicked: (User?) -> Unit,
    onRemoveOrReplace: (User) -> Unit
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
                .clickable { onCircleClicked(friend) },
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
                        .clickable { onRemoveOrReplace(friend) }
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
        friend?.displayName?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun FriendSelectionDialog(
    friendList: List<User>,
    onFriendSelected: (User) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (friendList.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Select a player") },
            text = {
                LazyColumn {
                    items(friendList) { friend ->
                        FriendItem(friend, onFriendSelected)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FriendItem(
    friend: User,
    onFriendSelected: (User) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFriendSelected(friend) }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = friend.profilePictureUrl,
            contentDescription = friend.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(friend.displayName, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ModifyFriendDialog(
    friend: User,
    onRemove: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Modify player") },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to remove ${friend.displayName}?",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRemove,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun Checkbox(selectedOption: MatchTypes, onOptionSelected: (MatchTypes) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { onOptionSelected(MatchTypes.friendly) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == MatchTypes.friendly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
        ) {
            Text("Friendly", fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { onOptionSelected(MatchTypes.competitive) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == MatchTypes.competitive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
        ) {
            Text("Competitive", fontSize = 12.sp)
        }
    }
}


@Composable
fun PrivateMatchSwitch(isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mark match as private",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
fun ClubItem(club: Club, onClick: () -> Unit) {
    val address = club.location.address + ", " + club.location.city
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            ),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 35.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    club.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    address,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Mo - Sa from ${club.openingHours.startTime}:00 to ${club.openingHours.endTime}:00",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )
            }
            AsyncImage(
                model = club.imageUrl,
                contentDescription = club.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
            )
        }
    }
}