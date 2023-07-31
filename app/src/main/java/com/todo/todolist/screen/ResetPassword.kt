package com.todo.todolist.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.todo.todolist.R
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(mainNavController: NavHostController) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "first") {
        composable("first") {
            Column{
                AppBar(stringResource(id = R.string.reset_password)) { mainNavController.popBackStack() }
                Spacer(modifier = Modifier.height(20.dp))
                ResetPasswordFirst()
            }
        }
    }
}

@Composable
fun ResetPasswordFirst() {
    var enterEmail by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    Column {
        TextField(
            value = enterEmail,
            singleLine = true,
            onValueChange = {enterEmail = it},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardActions = KeyboardActions(onDone = {
                searchEmail(enterEmail)
            }),
            placeholder = { Text(text = stringResource(id = R.string.enter_email_to_reset_password))}
        )
        Button(
            onClick = {
                scope.launch {
                    try {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(enterEmail)
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                      },
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.send_email_for_reset_password),
                style = MaterialTheme.typography.bodyMedium.copy(Color.White)
            )
        }
    }
}

private fun searchEmail(email: String) {
    val findEmail = FirebaseAuth.getInstance()
    findEmail.fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                println("signInMethods $signInMethods")
                val userUid = FirebaseAuth.getInstance().uid
                println("uid $userUid")
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { result ->
                        if(result.isSuccessful) {
                            println("result is successful ${result.result}") // 이곳 아무 값 없음..
                        }
                    }
            } else {
                println(task.exception)
            }
        }
}
