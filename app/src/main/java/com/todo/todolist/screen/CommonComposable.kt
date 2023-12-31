package com.todo.todolist.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.todo.todolist.R

@Composable
fun Loading(loading: MutableState<Boolean>) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    if(loading.value) {
        Box(modifier = Modifier
            .size(400.dp)
            .background(Color.Transparent)
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
            )
        }
    }
}


@Composable
fun AppBar(text: String, onBackClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()) {
        Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
            contentDescription = stringResource(id = R.string.back_description),
            modifier = Modifier
                .padding(start = 10.dp, top = 14.dp, end = 16.dp, bottom = 14.dp)
                .clickable(interactionSource = MutableInteractionSource(), indication = null) { onBackClick() })
        Text(text = text,
            style = MaterialTheme.typography.bodyLarge.copy(Color.Black),
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}

@Composable
fun Mean(whatsMean: String) {
    Text(
        text = whatsMean,
        style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
        modifier = Modifier.padding(all = 10.dp)
    )
}

@Composable
fun TextFieldForm(input: MutableState<String>, placeholder: String) {
    TextField(
        value = input.value,
        singleLine = true,
        onValueChange = {input.value = it},
        placeholder = { TextFieldPlaceholder(placeholder)},
        colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun TextFieldPlaceholder(placeholder: String) {
    Text(
        text = placeholder,
        style = MaterialTheme.typography.bodySmall.copy(Color.DarkGray)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmDialog(onDismiss: () -> Unit, content: String, confirmAction: () -> Unit, dismissAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = content
                )
            }
        },
        containerColor = Color.White,
        confirmButton = {
            Text(text = stringResource(id = R.string.yes),
                style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                modifier = Modifier.clickable { confirmAction() })
        },
        dismissButton = {
            Text(text = stringResource(id = R.string.no),
                style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
                modifier = Modifier.clickable { dismissAction() })
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

