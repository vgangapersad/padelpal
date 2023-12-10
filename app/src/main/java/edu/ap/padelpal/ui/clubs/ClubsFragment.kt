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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage


data class Club(
        val id: Int,
        val name: String,
        val address: String,
        val image: String
)

val clubs = listOf(
        Club(1, "Peak Performance Fitness", "123 Ascend Street", "gallery_icon"),
        Club(2, "Aquatic Quest Swim Center", "456 Wave Avenue", "gallery_icon"),
        Club(3, "Velocity Track Club", "789 Sprint Boulevard", "gallery_icon"),
        Club(4, "Summit Climbing Gym", "101 Boulder Road", "gallery_icon"),
        Club(5, "Iron Lifters Gym", "202 Muscle Lane", "gallery_icon"),
        Club(6, "Urban Cycle Hub", "303 Pedal Way", "gallery_icon"),
        Club(7, "Grand Slam Tennis Academy", "404 Serve Path", "gallery_icon"),
        Club(8, "Pinnacle Golf Range", "505 Fairway Trail", "gallery_icon"),
        Club(9, "Martial Arts Mastery Dojo", "606 Fist Alley", "gallery_icon"),
        Club(10, "Riverside Rowing Club", "707 Oar Plaza", "gallery_icon")
)

@Composable
fun ClubsScreen(navController: NavController) {
        var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

        Column {
                Spacer(modifier = Modifier.height(15.dp))
                SearchBar(state = searchQuery, onValueChange = { searchQuery = it })
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn {
                        val filteredClubs = clubs.filter {
                                it.name.contains(searchQuery.text, ignoreCase = true) ||
                                        it.address.contains(searchQuery.text, ignoreCase = true)
                        }
                        items(filteredClubs) { club ->
                                ClubItem(club = club, onClick = {
                                        navController.navigate("ClubDetail/${club.id}")
                                })
                        }
                }
        }
}
@Composable
fun ClubItem(club: Club, onClick: () -> Unit) {
        Card(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable(onClick = onClick) // Voeg hier de clickable modifier toe
                        .border(0.5.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.medium),
        ){

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
                                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                        club.address,
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                        AsyncImage(
                                model = "https://img.freepik.com/premium-vector/photo-icon-picture-icon-image-sign-symbol-vector-illustration_64749-4409.jpg?w=1060",
                                contentDescription = club.name,
                                modifier = Modifier
                                        .size(88.dp),
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

