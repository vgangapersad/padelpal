package edu.ap.padelpal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InformationChip(text: String, backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest) {
    Row(modifier = Modifier
        .clip(RoundedCornerShape(20))
        .background(backgroundColor)
        .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.inverseOnSurface, style = MaterialTheme.typography.labelSmall)
    }
}