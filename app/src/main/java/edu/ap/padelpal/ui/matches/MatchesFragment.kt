import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
import edu.ap.padelpal.models.MatchDetailsResponse
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.ui.components.IndeterminateCircularIndicator
import edu.ap.padelpal.ui.components.InformationChip
import edu.ap.padelpal.utilities.MatchUtils
import edu.ap.padelpal.utilities.formatDateForDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(userData: UserData?, navController: NavController) {
    val matchUtils = MatchUtils()

    val tabTitles = listOf("Upcoming", "Past")
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var isExtended by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    var pastMatches by remember {
        mutableStateOf<List<MatchDetailsResponse>>(emptyList())
    }

    var upcomingMatches by remember {
        mutableStateOf<List<MatchDetailsResponse>>(emptyList())
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { offset ->
                isExtended = offset == 0
            }
    }

    LaunchedEffect(key1 = pastMatches, key2 = upcomingMatches) {
        upcomingMatches = matchUtils.getMatchesWithDetails(true, false)
        pastMatches = matchUtils.getMatchesWithDetails(false, true)
    }

    val buttonWidth = animateDpAsState(
        targetValue = if (isExtended) 150.dp else 60.dp, label = "",
    )

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
                        "Matches",
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },

        ) { innerPadding ->
        Box {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = title) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                SearchBar(state = searchQuery, onValueChange = { searchQuery = it })
                Spacer(modifier = Modifier.height(10.dp))

                when (tabTitles[selectedTabIndex]) {
                    "Upcoming" -> UpcomingContent(userData, upcomingMatches, listState, navController)
                    "Past" -> PastContent(userData, pastMatches, listState, navController)
                }

            }
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate("newMatch")
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                text = {
                    AnimatedVisibility(
                        visible = isExtended,
                        enter = fadeIn() + expandHorizontally(),
                    ) {
                        Text("New match", modifier = Modifier.align(Alignment.Center))
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 95.dp, end = 16.dp)
                    .size(width = buttonWidth.value, height = 56.dp),

                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )

        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingContent(userData: UserData?, upcomingMatches: List<MatchDetailsResponse>, listState: LazyListState,navController: NavController) {
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = done) {
        delay(5000)
        done = true
    }

    if (upcomingMatches.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            items(upcomingMatches) { match ->
                if (userData != null) {
                    MatchCard(userData, match, onClick = {
                        navController.navigate("MatchDetail/${match.match.id},Matches")
                    })
                }
            }
            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    } else {
        if(done){
            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Text(text = "Nothing here yet")
                }
            }
        } else {
            IndeterminateCircularIndicator(label = "Loading upcoming matches")
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PastContent(userData: UserData?, pastMatches: List<MatchDetailsResponse>, listState: LazyListState, navController: NavController) {
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = done) {
        delay(5000)
        done = true
    }

    if(pastMatches.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            items(pastMatches) { match ->
                if (userData != null) {
                    MatchCard(userData, match, onClick = {
                        navController.navigate("MatchDetail/${match.match.id}")
                    })
                }
            }
            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    } else {
        if(done){
            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Text(text = "Nothing here yet")
                }
            }
        } else {
            IndeterminateCircularIndicator(label = "Loading past matches")
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchCard(user: UserData, match: MatchDetailsResponse, onClick: () -> Unit) {
    val matchUtils = MatchUtils()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isOrganizer = match.match.organizerId == user.userId
    var isJoined by remember { mutableStateOf(match.match.playerIds.contains(user.userId)) }
    var joinedPlayers by remember { mutableStateOf(match.match.playerIds.size) }
    var buttonText = "Join"

    if (isOrganizer) {
        buttonText = "Organizer"
    } else {
        if (isJoined) {
            buttonText = "Leave"
        }
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),

    ) {
        Box(modifier = Modifier.height(220.dp)) {
            AsyncImage(
                model = match.club.imageUrl,
                contentDescription = match.match.title,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radiusX = 4.dp,
                        radiusY = 4.dp,
                        edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp))
                    ),

                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.4f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.match.title,
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    InformationChip("${joinedPlayers}/${match.match.amountOfPlayers}")
                    Spacer(modifier = Modifier.width(8.dp))
                    InformationChip(match.match.matchType.name.capitalize(Locale("EN")))
                    Spacer(modifier = Modifier.width(8.dp))
                    InformationChip(match.match.genderPreference.name.capitalize(Locale("EN")))
                }
                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val startTime = LocalTime.ofSecondOfDay(match.booking.startTime)
                    val timeslot = "${startTime} - ${startTime.plusMinutes(match.booking.durationMinutes.toLong())}"
                    Icon(Icons.Default.DateRange, contentDescription = "Date", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "${formatDateForDisplay(LocalDate.ofEpochDay(match.booking.date))}  $timeslot", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "${match.club.name} (${match.club.location.city})", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }

                Button(
                    onClick = {
                        if(isOrganizer){
                            Toast.makeText(context, "You organized this match", Toast.LENGTH_LONG).show()
                        } else {
                            if (isJoined) {
                                val result = scope.launch {
                                    matchUtils.removePlayerByMatchId(match.match.id, user.userId)
                                }
                                if (!result.isCancelled) {
                                    isJoined = false
                                    joinedPlayers -= 1
                                    Toast.makeText(
                                        context,
                                        "You left ${match.match.title}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Try again later", Toast.LENGTH_LONG)
                                        .show()
                                }
                            } else {
                                val result = scope.launch {
                                    matchUtils.addPlayerByMatchId(match.match.id, user.userId)
                                }
                                if (!result.isCancelled) {
                                    isJoined = true
                                    joinedPlayers += 1
                                    Toast.makeText(
                                        context,
                                        "You joined ${match.match.title}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Try again later", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)){
                    Text(
                        text = buttonText,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(state: String, onValueChange: (String) -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 8.dp)
    ){
        TextField(
            value = state,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text("Search", color = MaterialTheme.colorScheme.onSecondaryContainer) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}