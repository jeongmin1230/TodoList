package com.todo.todolist.screen

import android.app.Activity
import android.content.Context
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
    val loading = remember { mutableStateOf(false) }

    val userEmail = remember { mutableStateOf(getStoredUserEmail(context))}
    val userPassword = remember { mutableStateOf(getStoredUserPassword(context))}

    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            Box {
                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    if(userEmail.value.isNotEmpty() && userPassword.value.isNotEmpty()) {
                        loading.value = true
                        Loading(loading)
                        loginUser(context as Activity, navController, userEmail, userPassword, loading)
                        loading.value = false
                    }
                    else {
                        LoginScreen(navController, loading)
                    }
                }
            }
        }

        composable("home") {
            Column {
                MainScreen()
            }
        }

        /** 텍스트 버튼 */
        composable(context.getString(R.string.registration)) {
            Column {
                RegistrationScreen(navController)
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController, loading: MutableState<Boolean>) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.to_do))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Spacer(modifier = Modifier.height(40.dp))
    Box {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(200.dp)) {
                LottieAnimation(
                    composition = composition,
                    progress = progress,
                )
            }

            TextField(
                value = email.value,
                singleLine = true,
                onValueChange = {email.value = it},
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
                value = password.value,
                singleLine = true,
                onValueChange = {password.value = it},
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
                enabled = email.value.isNotEmpty() && password.value.isNotEmpty(),
                onClick = {
                    loading.value = true
                    loginUser(context as Activity, navController, email, password, loading) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.LightGray,
                )) {
                Text(text = stringResource(id = R.string.login))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(id = R.string.registration),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(context.getString(R.string.registration)) },
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center))
            }
            Spacer(modifier = Modifier.padding(bottom = 12.dp))
        }
        Box(Modifier.align(Alignment.Center)) {
            Loading(loading)
        }
    }
}


private fun loginUser(activity: Activity, navController: NavHostController, email: MutableState<String>, password: MutableState<String>, loading: MutableState<Boolean>) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email.value.trim(), password.value.trim())
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                getUserData(activity, uid, navController, loading)
            } else {
                loading.value = false
                email.value = ""
                password.value = ""
                val error = task.exception.toString()
                if(error.contains(activity.getString(R.string.login_not_exists_email))){
                    Toast.makeText(activity, activity.getString(R.string.login_not_exists_email_toast), Toast.LENGTH_SHORT).show()
                }
                if(error.contains(activity.getString(R.string.login_invalid_password))) {
                    Toast.makeText(activity, activity.getString(R.string.login_invalid_password_toast), Toast.LENGTH_SHORT).show()
                }
                if(error.contains(activity.getString(R.string.email_format))) {
                    Toast.makeText(activity, activity.getString(R.string.login_email_badly_format), Toast.LENGTH_SHORT).show()
                }
                println(task.exception)
            }
        }
}

private fun getUserData(activity: Activity, uid: String, navController: NavHostController, loading: MutableState<Boolean>) {
    val userUid = FirebaseDatabase.getInstance().getReference("users")
    val userEmailRef = userUid.child(uid).child("info").child("email")
    val userNameRef = userUid.child(uid).child("info").child("name")
    val userPasswordRef = userUid.child(uid).child("info").child("password")

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
                            userPasswordRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val password = snapshot.getValue(String::class.java)
                                    if (password != null) {
                                        storeUserCredentials(activity, email, password.toString())
                                        UserInfo.userPassword = password.toString()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })
                            if (UserInfo.userEmail.isNotEmpty() && UserInfo.userName.isNotEmpty()) {
                                loading.value = false
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

private fun storeUserCredentials(activity: Activity, email: String, password: String) {
    val sharedPreferences = activity.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("email", email)
    editor.putString("password", password)
    editor.apply()
}