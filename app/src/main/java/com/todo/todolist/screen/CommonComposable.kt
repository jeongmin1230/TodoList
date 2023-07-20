package com.todo.todolist.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.todo.todolist.R

@Composable
fun AppBar(text: String, onBackClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()) {
        Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
            contentDescription = stringResource(id = R.string.back_description),
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clickable(interactionSource = MutableInteractionSource(), indication = null) { onBackClick() })
        Text(text = text,
            style = MaterialTheme.typography.bodyLarge.copy(Color.Black))
    }
}
