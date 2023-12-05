package edu.ap.padelpal.ui.profile

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ap.padelpal.R
import edu.ap.padelpal.presentation.sign_in.UserData


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

    val locations = listOf(
        "Antwerpen",
        "Gent",
        "Brussel",
        "Mechelen"
    )
    val bestHand = listOf(
        "Right-handed",
        "Left-handed",
        "both hands"
    )
    val courtPosition = listOf(
        "Baseline",
        "Net/Volleyer",
        "All-Court",
        "Defensive"
    )
    val matchType = listOf(
        "Singles",
        "Doubles",
        "Mixed doubles",
        "Team competitions"
    )
    val preferredTime = listOf(
        "Morning",
        "Afternoon",
        "Evening",
        "Night",
        "Weekdays",
        "Weekends",
        "Anytime"
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
            if (userData?.username != null) {
                var usernameState by remember { mutableStateOf(userData.username) }
                OutlinedTextField(
                    value = usernameState,
                    onValueChange = {usernameState = it},
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )
            }
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
