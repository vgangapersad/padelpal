package edu.ap.padelpal.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


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
    var selectedClub by remember { mutableStateOf(clubs[0]) }
    var isFocused by remember { mutableStateOf(false) }

    var selectedMatchOfType by remember { mutableStateOf("Competitive") }
    var selectedMaxPlayers by remember { mutableStateOf(2) }
    var selectedFriends by remember { mutableStateOf<List<Friend>>(listOf()) }
    var showFriendSelectionDialog by remember { mutableStateOf(false) }
    var currentFriendToModify by remember { mutableStateOf<Friend?>(null) }
    var showModifyFriendDialog by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    fun onAddFriend() {
        showFriendSelectionDialog = true
    }

    fun onModifyFriend(friend: Friend) {
        currentFriendToModify = friend
        showModifyFriendDialog = true
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("An awesome match title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .border(
                        1.dp,
                        if (isFocused) MaterialTheme.colorScheme.primary else Color.Gray,
                        RoundedCornerShape(4.dp)
                    )

            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                    readOnly = true,
                    value = selectedClub,
                    onValueChange = {},
                    label = { Text("Club") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
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
                                selectedClub = club
                                expanded = false
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
                        onRemoveOrReplace = { friend ->
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
                        selectedFriends = selectedFriends.filter { it != currentFriendToModify }
                        showModifyFriendDialog = false
                        currentFriendToModify = null
                    },
                    onDismissRequest = {
                        showModifyFriendDialog = false
                        currentFriendToModify = null
                    }
                )
            }
            Spacer(modifier = Modifier.height(35 .dp))
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
                    selectedOption = selectedMatchOfType,
                    onOptionSelected = { selectedMatchOfType = it }
                )
            }
            Spacer(modifier = Modifier.height(20 .dp))
            PrivateMatchSwitch(
                isChecked = isChecked,
                onCheckedChange = { isChecked = it }
            )

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
}@Composable
fun PlayerCircle(
    friend: Friend?,
    onCircleClicked: (Friend?) -> Unit,
    onRemoveOrReplace: (Friend) -> Unit
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
                    model =friend.photo,
                    contentDescription = friend.name,
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
                Icon(Icons.Filled.Add, contentDescription = "Add friend", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
        friend?.name?.let {
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
    friendList: List<Friend>,
    onFriendSelected: (Friend) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (friendList.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Select a Friend") },
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
    friend: Friend,
    onFriendSelected: (Friend) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFriendSelected(friend) }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = friend.photo,
            contentDescription = friend.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(friend.name, style = MaterialTheme.typography.bodyLarge)
    }
}
@Composable
fun ModifyFriendDialog(
    friend: Friend,
    onRemove: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Modify Friend") },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to remove ${friend.name}?",
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
fun Checkbox(selectedOption: String, onOptionSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { onOptionSelected("Competitive") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == "Competitive") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
        ) {
            Text("Competitive", fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { onOptionSelected("Friendly") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == "Friendly") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
        ) {
            Text("Friendly", fontSize = 12.sp)
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
