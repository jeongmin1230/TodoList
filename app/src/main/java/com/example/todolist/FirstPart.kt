package com.example.todolist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*

@Composable
fun Intro(navController: NavHostController, destination: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.to_do))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(400.dp)) {
            LottieAnimation(
                composition = composition,
                progress = progress,
            )
        }
        LoginButton(navController, destination)
    }
}

@Composable
fun LoginButton(navController: NavHostController, destination: String) {
    Button(onClick = { navController.navigate(destination) }) {
        Text(text = stringResource(id = R.string.login))
    }
}