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
import androidx.compose.ui.text.capitalize
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
import edu.ap.padelpal.models.Preferences
import edu.ap.padelpal.models.TimePreference
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.ui.components.IndeterminateCircularIndicator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.util.Locale


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
    var selectedPreferences: Preferences? by remember { mutableStateOf(null)}

    LaunchedEffect(key1 = userData?.userId) {
        if (userData != null) {
            coroutineScope.launch {
                val user = userRepository.getUserFromFirestore(userData.userId)
                usernameState = user?.displayName ?: ""
                selectedPreferences = user?.preferences
            }
        }
    }

    var isFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LazyColumn {
        if (selectedPreferences != null) {
            item {
                ProfilePicture(userData)
                OutlinedTextField(
                    value = usernameState.toString(),
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
                    selected = selectedPreferences!!.location,
                    onSelectedChanged = { newValue ->
                        selectedPreferences = selectedPreferences!!.copy(location = newValue)
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
                            val result = coroutineScope.launch {
                                userRepository.updateDisplayName(userData.userId, usernameState)
                                userRepository.updatePreferences(userData.userId,
                                    selectedPreferences!!
                                )
                            }
                            if (!result.isCancelled){
                                Toast.makeText(context, "Settings saved", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Try again later", Toast.LENGTH_LONG).show()
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
        } else {
            item {
                IndeterminateCircularIndicator(label = "Loading your settings")
            }
        }
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
            value = if (selected.name == "notSet") "Set your preference" else selected.name.capitalize(Locale("EN")),
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
            enumValues.filter { p -> p.name != "notSet" }.forEach { preference  ->
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
