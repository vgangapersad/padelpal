package edu.ap.padelpal.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ap.padelpal.data.firestore.UserRepository
import edu.ap.padelpal.models.MatchDetailsResponse
import edu.ap.padelpal.models.User
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.ui.components.IndeterminateCircularIndicator
import edu.ap.padelpal.ui.components.PersonalMatchCard
import edu.ap.padelpal.utilities.MatchUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: UserData?,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val userRepository = UserRepository()
    val matchUtils = MatchUtils()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var user by remember(userData?.userId) {
        mutableStateOf<User?>(null)
    }

    var matches by remember {
        mutableStateOf<List<MatchDetailsResponse>>(emptyList())
    }

    var done by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = done) {
        delay(5000)
        done = true
    }

    LaunchedEffect(key1 = userData?.userId) {
        if (userData != null) {
            coroutineScope.launch {
                val fetchedUser = userRepository.getUserFromFirestore(userData.userId)
                user = fetchedUser
            }
            matches = matchUtils.getJoinedMatchesWithDetailsByUser(userData.userId)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        "Profile",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("Settings") }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            item {
                ProfileInformation(userData = userData, user = user, navController = navController)
                Spacer(modifier = Modifier.height(20.dp))
            }
            if (matches.isNotEmpty()) {
                items(matches) { match ->
                    if (userData != null) {
                        PersonalMatchCard(userData, match, onClick = {
                            navController.navigate("MatchDetail/${match.match.id}")
                        })
                    }
                }
            } else {
                item {
                    if (done) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Row(modifier = Modifier.align(Alignment.Center)) {
                                Text(text = "Nothing here yet")
                            }
                        }
                    } else {
                        IndeterminateCircularIndicator(label = "Loading ${user?.displayName}'s matches")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ProfileInformation(userData: UserData?, user: User?, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
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
            Column {
                if (userData?.username != null) {
                    user?.let {
                        Text(
                            text = it.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Antwerpen - België",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }

    // Green bar section
    Spacer(modifier = Modifier.height(15.dp))
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
