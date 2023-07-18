package com.todo.todolist.screen

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.todo.todolist.*
import com.todo.todolist.R
import com.todo.todolist.ui.theme.TodoListTheme

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Screen()
                }
            }
        }
    }
}

@Composable
fun Screen() {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Column {
                LoginScreen(navController)
            }
        }

        composable("home") {
            Column {
                MainScreen()
            }
        }

        /** 텍스트 버튼 */
        composable(context.getString(R.string.find_id)) {
            Column {
                FindIdScreen()
            }
        }
        composable(context.getString(R.string.reset_password)) {
            Column {
                ResetPasswordScreen(navController)
            }
        }
        composable(context.getString(R.string.registration)) {
            Column {
                RegistrationScreen(navController)
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.to_do))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Spacer(modifier = Modifier.height(40.dp))
    Column(Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(200.dp)) {
            LottieAnimation(
                composition = composition,
                progress = progress,
            )
        }
        TextField(
            value = email,
            singleLine = true,
            onValueChange = {email = it},
            placeholder = {
                Text(text = stringResource(id = R.string.email),
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
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = password,
            singleLine = true,
            onValueChange = {password = it},
            placeholder = {
                Text(text = stringResource(id = R.string.password),
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
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { loginUser(context as Activity, navController, email, password) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()) {
            Text(text = stringResource(id = R.string.login))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text(text = stringResource(id = R.string.reset_password),
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigate(context.getString(R.string.reset_password)) },
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center))
            Text(text = stringResource(id = R.string.registration),
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigate(context.getString(R.string.registration)) },
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center))
        }
        Spacer(modifier = Modifier.padding(bottom = 12.dp))
    }
}

private fun loginUser(context: Activity, navController: NavHostController, email: String, password: String) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email.trim(), password.trim())
        .addOnCompleteListener(context) { task ->
            if (task.isSuccessful) {
                navController.navigate("home")
                println("로그인 성공")
            } else {
                println(task.exception)
                println("로그인 실패")
            }
        }
}
