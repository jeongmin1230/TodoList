package com.todo.todolist.screen

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next)
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
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            onClick = { loginUser(context as Activity, navController, email, password) },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.LightGray
            ),
        ) {
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

private fun loginUser(activity: Activity, navController: NavHostController, email: String, password: String) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email.trim(), password.trim())
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val uid = user?.uid ?: ""
                getUserData(uid, navController)
            } else println(task.exception)
        }
}

private fun getUserData(uid: String, navController: NavHostController) {
    val userUid = FirebaseDatabase.getInstance().getReference("users")
    val userEmailRef = userUid.child(uid).child("info").child("email")
    val userNameRef = userUid.child(uid).child("info").child("name")

    userEmailRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val email = snapshot.getValue(String::class.java)
            if (email != null) {
                UserInfo.userEmail = email

                userNameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.getValue(String::class.java)
                        if (name != null) {
                            UserInfo.userName = name
                            if (UserInfo.userEmail.isNotEmpty() && UserInfo.userName.isNotEmpty()) {
                                navController.navigate("home")
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    })
}