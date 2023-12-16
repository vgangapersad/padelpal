package edu.ap.padelpal.ui.matches

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import kotlin.math.roundToInt


val clubs = listOf(
    "Not set",
    "Soccer Strikers",
    "Basketball Blazers",
    "Tennis Titans",
    "Volleyball Vipers",
    "Golf Gladiators",
)
data class Friend(val name: String, val photo: String)

val friends = listOf(
        Friend("Sarah","https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=1000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cGVyc29ufGVufDB8fDB8fHww"),
        Friend("Zeynep", "https://www.verywellmind.com/thmb/pwEmuUJ6KO9OF8jeiQCDyKnaVQI=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/GettyImages-1187609003-73c8baf32a6a46a6b84fe931e0c51e7e.jpg"),
        Friend("Zahraa", "https://images.squarespace-cdn.com/content/v1/51f6d482e4b08b0bde0f6d65/1622602815444-DHNPWK4EPWEY5VQA18F3/self_worth_2.jpg"),
    )


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMatchScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    var title by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(clubs[0]) }
    var isFocused by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableStateOf(2f) }

    var showDialog by remember { mutableStateOf(false) }
    var friendToSelectIndex by remember { mutableStateOf(-1) }
    var selectedFriends by remember { mutableStateOf(List(sliderPosition.toInt()) { Friend("", "") }) }
    var selectedMatchType by remember { mutableStateOf("competitive") } // Begin met "competitive" als de standaard geselecteerde waarde


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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .border(1.dp, if (isFocused) MaterialTheme.colorScheme.primary else Color.Gray, RoundedCornerShape(4.dp))

            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .onFocusChanged { focusState -> isFocused = focusState.isFocused}
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                    readOnly = true,
                    value = selectedLocation,
                    onValueChange = {},
                    label = { Text("Club") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent ,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    clubs.forEach { club ->
                        DropdownMenuItem(
                            text = { Text(club) },
                            onClick = {
                                selectedLocation = club
                                expanded = false
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(35 .dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Max players:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(15.dp))
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        val newCount = newValue.roundToInt()
                        if (newCount > selectedFriends.size) {
                            // Voeg nieuwe lege vrienden toe als het aantal is verhoogd
                            selectedFriends = selectedFriends + List(newCount - selectedFriends.size) { Friend("", "") }
                        } else if (newCount < selectedFriends.size) {
                            // Behoud de ingevulde vrienden en verwijder alleen extra lege instanties
                            selectedFriends = selectedFriends.take(newCount)
                        }
                        sliderPosition = newValue
                    },
                    valueRange = 0f..10f,
                    steps = 0,
                    modifier = Modifier.weight(1f),
                    onValueChangeFinished = {
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    ),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .padding(1.dp)
                        ) {
                            Text(
                                text = sliderPosition.roundToInt().toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
            }

            // Toon de FriendSelectionDialog als showDialog waar is
            if (showDialog) {
                FriendSelectionDialog(
                    friendList = friends.filterNot { selectedFriends.contains(it) },
                    onFriendSelected = { friend ->
                        // Werk de lijst met geselecteerde vrienden bij
                        val currentSelectedIndex = selectedFriends.indexOfFirst { it.name.isEmpty() }
                        if (currentSelectedIndex != -1) {
                            val updatedFriends = selectedFriends.toMutableList()
                            updatedFriends[currentSelectedIndex] = friend
                            selectedFriends = updatedFriends
                        }
                        showDialog = false
                    }
                )
            }

            CircleSelector(
                selectedFriends = selectedFriends,
                onSelectFriend = { _, index ->
                    // Stel de index in van de geselecteerde vriend en toon de dialoog
                    if (selectedFriends[index].name.isEmpty()) {
                        friendToSelectIndex = index
                        showDialog = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(35 .dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Match of type: ",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(10.dp))

                MatchOfType()
            }
            Spacer(modifier = Modifier.height(20 .dp))
            PrivateMatchSwitch()

        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CircleSelector(
    selectedFriends: List<Friend>,
    onSelectFriend: (Friend, Int) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp),
    ) {
        selectedFriends.forEachIndexed { index, friend ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .clickable { onSelectFriend(friend, index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (friend.name.isNotEmpty()) {
                        Image(
                            painter = rememberImagePainter(
                                data = friend.photo,
                                builder = {
                                    transformations(coil.transform.CircleCropTransformation())
                                }
                            ),
                            contentDescription = friend.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Toevoegen",
                            modifier = Modifier.size(24.dp),
                            tint =MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
                if (friend.name.isNotEmpty()) {
                    Text(
                        text = friend.name,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun FriendSelectionDialog(
    friendList: List<Friend>,
    onFriendSelected: (Friend) -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select a Friend") },
            text = {
                LazyColumn {
                    items(friendList) { friend ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onFriendSelected(friend)
                                    showDialog = false
                                }
                                .padding(8.dp)
                        ) {
                            Image(
                                painter = rememberImagePainter(
                                    data = friend.photo,
                                    builder = {
                                        crossfade(true)
                                    }
                                ),
                                contentDescription = friend.name,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(18.dp))
                            Text(friend.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MatchOfType() {
    var selectedOption by remember { mutableStateOf("Competitive") }

    Row(
        modifier = Modifier
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Button(
            onClick = { selectedOption = "Competitive" },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == "Competitive") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.size(width = 120.dp, height = 35.dp)
        ) {
            Text("Competitive", fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { selectedOption = "Friendly" },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == "Friendly") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.size(width = 110.dp, height = 35.dp)
        ) {
            Text("Friendly", fontSize = 12.sp)
        }
    }
}


@Composable
fun PrivateMatchSwitch() {
    var isChecked by remember { mutableStateOf(false) }

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
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
