package edu.ap.padelpal.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ap.padelpal.data.firestore.UserRepository
import edu.ap.padelpal.models.User
import edu.ap.padelpal.presentation.sign_in.UserData
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    userData: UserData?,
    navController: NavController
    ) {

    val coroutineScope = rememberCoroutineScope()
    val userRepository = UserRepository()

    var user by remember(userData?.userId) {
        mutableStateOf<User?>(null)
    }

    // Fetch the user data when the screen is created
    LaunchedEffect(key1 = userData?.userId) {
        if (userData != null) {
            coroutineScope.launch {
                val fetchedUser = userRepository.getUserFromFirestore(userData.userId)
                // Update the user data using the remember variable
                user = fetchedUser
            }
        }
    }
    Surface(
        modifier = Modifier.padding(10.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (userData?.profilePictureUrl != null) {
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (userData?.username != null) {
                        user?.let {
                            Text(
                                text = it.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                    }
                    Text(
                        "Antwerpen - België", color = MaterialTheme.colorScheme.secondary)
                }

                IconButton(onClick = {
                    navController.navigate("Settings")
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "settings",
                    )
                }
            }
            // Green bar section
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 80.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    user?.let {
                        Text(
                            text = it.level.toString(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyLarge,

                            )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Level",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    user?.let {
                        Text(
                            text = it.matchesPlayed.toString(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyLarge,

                            )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Played",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    user?.let {
                        Text(
                            text = it.matchesWon.toString(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Wins",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
