package edu.ap.padelpal.presentation.sign_in

import android.content.ContentValues.TAG
import android.icu.number.NumberFormatter.UnitWidth
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.padelpal.data.firestore.UserRepository
import edu.ap.padelpal.models.Preferences
import edu.ap.padelpal.models.User

@Composable
fun SignInScreen(
    userData: UserData?,
    state: SignInState,
    onSignInClick: () -> Unit
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
            .padding(16.dp),
    ) {
        Button(onClick = {
            onSignInClick()
        }) {
            Text(text = "Sign in")
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5", showSystemUi = true)
@Composable
fun SignInScreenPreview() {

}