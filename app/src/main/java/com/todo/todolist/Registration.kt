package com.todo.todolist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegistrationScreen(mainNavController: NavHostController) {
    val menuComposition = stringArrayResource(R.array.composition)
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "first") {
        composable("first") {
            Column {
                AppBar(stringResource(id = R.string.registration)) { mainNavController.popBackStack() }
                Spacer(modifier = Modifier.height(20.dp))
                InputInformation(navController)
            }
        }
        composable("second") {
            Column {
                Complete(menuComposition[0], mainNavController)
            }
        }
    }
}

@Composable
fun InputInformation(navController: NavHostController) {
    val context = LocalContext.current
    val errorString = stringArrayResource(id = R.array.registration_error)
    val errorMessage = stringArrayResource(id = R.array.registration_error_message)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Column {
        TextField(
            value = email,
            onValueChange = {email = it},
            placeholder = {
                Text(text = stringResource(id = R.string.enter_email),
                    style = MaterialTheme.typography.bodyMedium)
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = {password = it},
            placeholder = {
                Text(text = stringResource(id = R.string.enter_password),
                    style = MaterialTheme.typography.bodyMedium)
            },
            visualTransformation = PasswordVisualTransformation('*'),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = confirmPassword,
            onValueChange = {confirmPassword = it},
            placeholder = {
                Text(text = stringResource(id = R.string.enter_password_again),
                    style = MaterialTheme.typography.bodyMedium)
            },
            visualTransformation = PasswordVisualTransformation('*'),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(enabled = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty(),
            onClick = { performSignup(errorString, errorMessage, context, navController, email, password) },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()) {
        Text(text = stringResource(id = R.string.complete))
    }
}

@Composable
fun Complete(destination: String, navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.complete))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)
    
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Box(modifier = Modifier
            .size(200.dp)) {
            LottieAnimation(
                composition = composition,
                progress = progress,
            )
        }
        Button(onClick = { navController.navigate(destination) }) {
            Text(text = stringResource(id = R.string.do_login))
        }
    }
}

@SuppressLint("ResourceType")
private fun performSignup(errorString: Array<String>, errorMessage: Array<String>, context: Context, navController: NavHostController, email: String, password: String) {
    val activity = context as Activity
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("second")
            } else {
                println(task.exception)
                if(task.exception.toString().contains(errorString[0])) {
                    Toast.makeText(activity, errorMessage[0], Toast.LENGTH_SHORT).show()
                }
                if(task.exception.toString().contains(errorString[1])) {
                    Toast.makeText(activity, errorMessage[1], Toast.LENGTH_SHORT).show()
                }
                if(task.exception.toString().contains(errorString[2])) {
                    Toast.makeText(activity, errorMessage[2], Toast.LENGTH_SHORT).show()
                }
            }
        }
}