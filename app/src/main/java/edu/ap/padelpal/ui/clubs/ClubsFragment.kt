package edu.ap.padelpal.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.ap.padelpal.data.firestore.ClubRepository
import edu.ap.padelpal.models.Club


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val clubRepository = ClubRepository()
    var clubs by remember { mutableStateOf(emptyList<Club>()) }

    LaunchedEffect(key1 = clubRepository) {
        clubs = clubRepository.getAllClubs()
    }

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
                        "Clubs",
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
            Spacer(modifier = Modifier.height(10.dp))
            SearchBar(state = searchQuery, onValueChange = { searchQuery = it })
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {
                val filteredClubs = clubs.filter {
                    it.name.contains(searchQuery.text, ignoreCase = true) ||
                            it.location.address.contains(searchQuery.text, ignoreCase = true)
                }
                items(filteredClubs) { club ->
                    ClubItem(club = club, onClick = {
                        navController.navigate("ClubDetail/${club.id}")
                    })
                }
                item{
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun ClubItem(club: Club, onClick: () -> Unit) {
    val address = club.location.address + ", " + club.location.city
    Card(
        modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(onClick = onClick)
                .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                ),
    ) {

        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 35.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    club.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    address,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Mo - Sa from ${club.openingHours.startTime}:00 to ${club.openingHours.endTime}:00",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )
            }
            AsyncImage(
                model = club.imageUrl,
                contentDescription = club.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .height(100.dp)
                        .width(120.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(state: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    TextField(
        value = state.text,
        onValueChange = { newText -> onValueChange(TextFieldValue(newText)) },
        textStyle = LocalTextStyle.current.copy(color = Color.White), // Zet hier de tekstkleur op wit
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
        },
        modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
    )
}

