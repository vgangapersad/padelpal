package edu.ap.padelpal.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.R
import edu.ap.padelpal.data.firestore.UserRepository
import edu.ap.padelpal.models.CourtPositionPreference
import edu.ap.padelpal.models.HandPreference
import edu.ap.padelpal.models.LocationPreference
import edu.ap.padelpal.models.MatchTypePreference
import edu.ap.padelpal.models.Preferences
import edu.ap.padelpal.models.TimePreference
import edu.ap.padelpal.models.User
import edu.ap.padelpal.presentation.sign_in.UserData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userData: UserData?,
    navController: NavController,
    onSignOut: () -> Unit
) {
   Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            SettingsContent(
                modifier = Modifier.padding(innerPadding),
                userData = userData,
            )
        }
    )
}



@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    userData: UserData?) {

    var usernameState by remember { mutableStateOf(userData?.username ?: "") }
    val coroutineScope = rememberCoroutineScope()
    val userRepository = UserRepository()
    // Fetch the display name from Firestore when the screen is created
    LaunchedEffect(key1 = userData?.userId) {
        if (userData != null) {
            coroutineScope.launch {
                val user = userRepository.getUserFromFirestore(userData.userId)
                usernameState = user?.displayName ?: ""
            }
        }
    }
    val locations = listOf(
        "Antwerpen",
        "Gent",
        "Brussel",
        "Mechelen",
        "Not set"
    )
    val bestHand = listOf(
        "Right-handed",
        "Left-handed",
        "Both hands",
        "Not set"
    )
    val courtPosition = listOf(
        "Both sides",
        "Backhand",
        "Forehand",
        "Not set"
    )
    val matchType = listOf(
        "Competitive",
        "Friendly",
        "Both",
        "Not set"
    )
    val preferredTime = listOf(
        "Morning",
        "Afternoon",
        "Evening",
        "All day",
        "Not set"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        ProfilePicture(userData)

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        )
        {
                OutlinedTextField(
                    value = usernameState,
                    onValueChange = {usernameState = it},
                    label = { Text("Display Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )
            Selection(
                selectionData = locations,
                selectionLabel = "Location",
                icon = Icons.Filled.LocationOn,
                iconResource = null
            )
            Selection(
                selectionData = bestHand,
                selectionLabel = "Best hand",
                icon = null,
                iconResource = painterResource(id = R.drawable.hand)
            )
            Selection(
                selectionData = courtPosition,
                selectionLabel = "Court position",
                icon = null,
                iconResource = painterResource(id = R.drawable.position)
            )
            Selection(
                selectionData = matchType,
                selectionLabel = "Match type",
                icon = null,
                iconResource = painterResource(id = R.drawable.padel)
            )
            Selection(
                selectionData = preferredTime,
                selectionLabel = "Preferred time to play",
                icon = null,
                iconResource = painterResource(id = R.drawable.sun)
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
        val context = LocalContext.current
        Button(
            onClick = {
                if (userData != null) {
                    coroutineScope.launch {
                        saveDisplayNameToFirestore(userData.userId, usernameState, context)

                    }
                }
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) {
            Text("Save")
        }
    }
}

// Function to save the display name to Firestore
private suspend fun saveDisplayNameToFirestore(uid: String, displayName: String, context: Context) {
    try {
        val db = Firebase.firestore
        val userDocument = db.collection("users").document(uid)

        userDocument.update("displayName", displayName).await()
        Toast.makeText(context, "Display Name Saved", Toast.LENGTH_SHORT).show()
        // Handle success if needed
    } catch (e: Exception) {
        // Handle exception
    }
}

@Composable
fun ProfilePicture(userData: UserData?) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 25.dp)
    ) {
        if (userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Selection(
    selectionData: List<String>,
    selectionLabel: String,
    icon: ImageVector?,
    iconResource: Painter?
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = Modifier
            .padding(bottom = 15.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .menuAnchor(),
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
            },
            label = { Text(selectionLabel) },
            trailingIcon = {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = selectionLabel,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (iconResource != null) {
                    Icon(
                        painter = iconResource,
                        contentDescription = selectionLabel,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
        )

        // filter options based on text field value
        val filteringOptions = selectionData.filter { it.contains(textFieldValue, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                filteringOptions.forEach { selectedData ->
                    DropdownMenuItem(
                        text = { Text(selectedData) },
                        onClick = {
                            textFieldValue = selectedData
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
