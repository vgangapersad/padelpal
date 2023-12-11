package edu.ap.padelpal.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.R

@Composable
fun SignInScreen(
    userData: UserData?,
    state: SignInState,
    onSignInClick: () -> Unit,
) {
    val context = LocalContext.current
    val db = Firebase.firestore
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color.Black, Color.Transparent)))
    ) {
        Image(
            painter = painterResource(id = R.drawable.padel_tournament),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Donkere overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PadelPal",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(end = 15.dp, bottom = 8.dp)            )
            Text(
                text = "An app for padel enthusiasts to connect with their pals.",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = { onSignInClick() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp),
            contentPadding = PaddingValues(
                start = 25.dp,
                top = 12.dp,
                end = 25.dp,
                bottom = 12.dp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Sign In",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing + 16.dp))
            Text(text = "Sign in with Google")
        }
    }
}
@Preview(showBackground = true, device = "id:pixel_5", showSystemUi = true)
@Composable
fun SignInScreenPreview() {

}