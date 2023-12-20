package edu.ap.padelpal.ui.components

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import edu.ap.padelpal.models.MatchDetailsResponse
import edu.ap.padelpal.presentation.sign_in.UserData
import edu.ap.padelpal.utilities.MatchUtils
import edu.ap.padelpal.utilities.formatDateForDisplay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonalMatchCard(user: UserData, match: MatchDetailsResponse, onClick: () -> Unit) {
    val matchUtils = MatchUtils()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isOrganizer = match.match.organizerId == user.userId
    var isJoined by remember { mutableStateOf(match.match.playerIds.contains(user.userId)) }
    var joinedPlayers by remember { mutableStateOf(match.match.playerIds.size) }
    var buttonText = "Join"

    if (isOrganizer) {
        buttonText = "Organized"
    } else {
        if (isJoined) {
            buttonText = "Leave"
        }
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
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