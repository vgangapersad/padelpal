package edu.ap.padelpal.ui.clubs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp



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
fun ClubsScreen() {
        LazyColumn {
                items(clubs) { club ->
                        ClubItem(club)
                }
        }
}

@Composable
fun ClubItem(club: Club) {
        val imageResId = LocalContext.current.resources.getIdentifier(
                club.image, "drawable", LocalContext.current.packageName
        )
        val imagePainter = painterResource(id = imageResId)

        Card(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
        ) {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp), // Removed padding here
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween // Ensures the image is on the far right
                ) {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f) // Takes up the available space, pushing the image to the right
                                        .padding(start = 15.dp),
                                verticalArrangement = Arrangement.Center
                        ) {
                                Text(
                                        club.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)

                                )
                                Text(
                                        club.address,
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                        Image(
                                painter = imagePainter,
                                contentDescription = club.name,
                                modifier = Modifier
                                        .size(88.dp)
                        )
                }
        }
}