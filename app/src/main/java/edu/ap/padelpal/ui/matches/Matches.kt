
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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


data class PadelTournament(
    val id: String,
    val title: String,
    val maxPlayers: Int,
    val playersJoined: Int,
    val gameType: String,
    val date: String,
    val time: String,
    val location: String
)

val tournaments = listOf(
    PadelTournament(
        id = "1",
        title = "Spring Padel Open",
        maxPlayers = 8,
        playersJoined = 2,
        gameType = "Mixed",
        date = "2023-04-10",
        time = "10:00",
        location = "Padel Park A"
    ),
    PadelTournament(
        id = "2",
        title = "Summer Slam",
        maxPlayers = 6,
        playersJoined = 4,
        gameType = "Men Only",
        date = "2023-06-15",
        time = "15:00",
        location = "City Padel Club"
    ),
    PadelTournament(
        id = "3",
        title = "Women's Padel Challenge",
        maxPlayers = 4,
        playersJoined = 1,
        gameType = "Women Only",
        date = "2023-07-20",
        time = "09:00",
        location = "Beachside Padel"
    ),
    PadelTournament(
        id = "4",
        title = "Autumn Padel Fest",
        maxPlayers = 10,
        playersJoined = 3,
        gameType = "Mixed",
        date = "2023-10-05",
        time = "14:00",
        location = "Mountain View Padel"
    ),
    PadelTournament(
        id = "5",
        title = "Winter Padel Gala",
        maxPlayers = 12,
        playersJoined = 2,
        gameType = "Mixed",
        date = "2023-12-10",
        time = "18:00",
        location = "Downtown Padel Arena"
    ),
    PadelTournament(
        id = "6",
        title = "Pro Padel League",
        maxPlayers = 16,
        playersJoined = 4,
        gameType = "Men Only",
        date = "2023-05-22",
        time = "17:00",
        location = "Pro Padel Court"
    ),
    PadelTournament(
        id = "7",
        title = "Junior Padel Tournament",
        maxPlayers = 6,
        playersJoined = 0,
        gameType = "Mixed",
        date = "2023-08-15",
        time = "10:00",
        location = "Suburban Padel Club"
    ),
    PadelTournament(
        id = "8",
        title = "Senior Padel Championship",
        maxPlayers = 8,
        playersJoined = 1,
        gameType = "Mixed",
        date = "2023-09-07",
        time = "16:00",
        location = "Central Padel Courts"
    ),
    PadelTournament(
        id = "9",
        title = "Holiday Padel Bash",
        maxPlayers = 4,
        playersJoined = 3,
        gameType = "Women Only",
        date = "2023-11-30",
        time = "20:00",
        location = "Riverside Padel"
    ),
    PadelTournament(
        id = "10",
        title = "End of Year Padel Showdown",
        maxPlayers = 14,
        playersJoined = 2,
        gameType = "Mixed",
        date = "2023-12-28",
        time = "13:00",
        location = "Highland Padel Complex"
    )
)




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen() {
    val tabTitles = listOf("Upcoming", "Past")
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

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
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
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
                "Upcoming" -> UpcomingContent(searchQuery)
                "Past" -> PastContent()
            }
        }

    }

}




@Composable
fun UpcomingContent(searchQuery: String) {
    val filteredTournaments = tournaments.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.gameType.contains(searchQuery, ignoreCase = true) ||
                it.date.contains(searchQuery, ignoreCase = true) ||
                it.time.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }
    LazyColumn {
        items(filteredTournaments) { tournament ->
            MatchCard(tournament)
        }
    }
}

@Composable
fun PastContent() {

}

@Composable
fun MatchCard(tournament: PadelTournament) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 14.dp, end = 14.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.height(220.dp)) {
            AsyncImage(
                model = "https://img.redbull.com/images/c_crop,x_1295,y_0,h_2487,w_1989/c_fill,w_400,h_500/q_auto:low,f_auto/redbullcom/2022/4/1/hrxm462vtwdfpqvf4rdh/alejandro-galan-padel-action",
                contentDescription = tournament.title,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radiusX = 8.dp,
                        radiusY = 8.dp,
                        edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp))
                    ),

                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tournament.title,
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    InformationCard("${tournament.playersJoined}/${tournament.maxPlayers}")
                    Spacer(modifier = Modifier.width(8.dp))
                    InformationCard(tournament.gameType)
                }
                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "${tournament.date} - ${tournament.time}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = tournament.location, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }

                Button(
                    onClick = { /* TODO: Handle click */ },
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)){
                    Text("Join", color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun InformationCard(text: String) {
    Row(modifier = Modifier
        .clip(RoundedCornerShape(20))
        .background(Color.White)
        .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = Color.Black, style = MaterialTheme.typography.labelSmall)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(state: String, onValueChange: (String) -> Unit) {
    TextField(
        value = state,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = { Text("Search", color = MaterialTheme.colorScheme.onPrimary) },
        singleLine = true,
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}