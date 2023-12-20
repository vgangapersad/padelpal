package edu.ap.padelpal.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import edu.ap.padelpal.models.TimePreference
import edu.ap.padelpal.presentation.sign_in.UserData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userData: UserData?,
    navController: NavController,
    onSignOut: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }


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
        }
    ) { innerPadding ->
        Box( modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {
            SettingsContent(
                userData = userData,
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(userData: UserData?) {
    var usernameState by remember { mutableStateOf(userData?.username ?: "") }
    val coroutineScope = rememberCoroutineScope()
    val userRepository = UserRepository()
    var selectedLocation by remember { mutableStateOf(LocationPreference.antwerpen)}


    // Fetch the display name from Firestore when the screen is created
    LaunchedEffect(key1 = userData?.userId) {
        if (userData != null) {
            coroutineScope.launch {
                val user = userRepository.getUserFromFirestore(userData.userId)
                usernameState = user?.displayName ?: ""
                selectedLocation = user?.preferences?.location!!
            }
        }
    }

    var selectedBestHand by remember { mutableStateOf(HandPreference.leftHanded)}
    var selectedCourtPosition by remember { mutableStateOf(CourtPositionPreference.bothSides)}
    var selectedMatchType by remember { mutableStateOf(MatchTypePreference.friendly)}
    var selectedPreferredTime by remember { mutableStateOf(TimePreference.morning)}
    var isFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }



    LazyColumn {
        item {
            ProfilePicture(userData)
            OutlinedTextField(
                value = if (usernameState != null) usernameState.toString() else "",
                onValueChange = { usernameState = it },
                label = { Text("Display Name") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    autoCorrect = true,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )
        }

        item {

            Selection(
                label = "Location",
                selected = selectedLocation,
                onSelectedChanged = { newValue ->
                    selectedLocation = newValue
                },
                enumValues = LocationPreference.values(),
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
        item {
            val context = LocalContext.current
            Button(
                onClick = {
                    if (userData != null) {
                        coroutineScope.launch {
                            saveDisplayNameToFirestore(userData.userId, usernameState, context)
                            saveSelectionsToFirestore(
                                userData.userId,
                                selectedLocation,
                                context)
                        }
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .height(48.dp)
                    .fillMaxSize(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(100.dp))
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
private suspend fun saveSelectionsToFirestore(
    uid: String,
    selectedLocation: LocationPreference,
    context: Context
) {
    try {
        val db = Firebase.firestore
        val userDocument = db.collection("users").document(uid)

        userDocument.update(
            "preferences.location", selectedLocation).await()
        Toast.makeText(context, "Preferences Saved", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
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
fun <T: Enum<T>> Selection(
    label: String,
    selected: T,
    onSelectedChanged: (T) -> Unit,
    enumValues: Array<T>,
    icon: ImageVector? = null,
    iconResource: Painter? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .border(
                1.dp,
                if (isFocused) MaterialTheme.colorScheme.primary else Color.Gray,
                RoundedCornerShape(4.dp)
            )) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
            readOnly = true,
            value = selected.name,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (iconResource != null) {
                    Icon(
                        painter = iconResource,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
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
            enumValues.forEach { preference  ->
                DropdownMenuItem(
                    text = { Text(preference.name.capitalize()) },
                    onClick = {
                        onSelectedChanged(preference)
                        expanded = false
                    },
                )
            }
        }
    }

}

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun <T : Enum<T>> Selection(
//    label: String,
//    selected: T,
//    enumValues: Array<T>,
//    icon: ImageVector?,
//    iconResource: Painter?,
//    getName: @Composable (T) -> String
//){
//    var isFocused by remember { mutableStateOf(false) }
//    var expanded by remember { mutableStateOf(false) }
//    var selectedPreference by remember { mutableStateOf(selected) }
//
//    ExposedDropdownMenuBox(
//        expanded = expanded,
//        onExpandedChange = { expanded = !expanded },
//        modifier = Modifier
//            .border(
//                1.dp,
//                if (isFocused) MaterialTheme.colorScheme.primary else Color.Gray,
//                RoundedCornerShape(4.dp)
//            )
//    ) {
//        TextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .menuAnchor()
//                .onFocusChanged { focusState -> isFocused = focusState.isFocused }
//                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
//            readOnly = true,
//            value = getName(selected),
//            onValueChange = {},
//            label = { Text(label) },
//            trailingIcon = {
//                if (icon != null) {
//                    Icon(
//                        imageVector = icon,
//                        contentDescription = label,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                } else if (iconResource != null) {
//                    Icon(
//                        painter = iconResource,
//                        contentDescription = label,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//            },
//            colors = TextFieldDefaults.textFieldColors(
//                containerColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedIndicatorColor = Color.Transparent
//            )
//        )
//
//        ExposedDropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//        ) {
//            enumValues.forEach { preference ->
//                DropdownMenuItem(
//                    text = { Text(getName(preference)) },
//                    onClick = {
//                        selectedPreference = preference
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}
