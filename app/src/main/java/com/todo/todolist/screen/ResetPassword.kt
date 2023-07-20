package com.todo.todolist.screen

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

@Composable
fun ResetPasswordScreen(mainNavController: NavHostController) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "first") {
        composable("first") {
            Column{
                AppBar(stringResource(id = R.string.registration)) { mainNavController.popBackStack() }
                Spacer(modifier = Modifier.height(20.dp))
                ResetPasswordFirst()
            }
        }
    }
}

@Composable
fun ResetPasswordFirst() {
    var enterEmail by remember { mutableStateOf("") }
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
    /*println("재설정" + email)
    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                if (!signInMethods.isNullOrEmpty()) {
                    println(signInMethods)
                    // 이메일 주소에 해당하는 사용자가 존재하는 경우
                    val userUid = FirebaseAuth.getInstance().getUserByEmail(email)?.uid
                    if (userUid != null) {
                        // 비밀번호 재설정을 위해 사용자의 이메일 주소로 인증 메일 보내기
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { resetTask ->
                                if (resetTask.isSuccessful) {
                                    println("Password reset email sent successfully to $email")
                                } else {
                                    println("Failed to send password reset email: ${resetTask.exception?.message}")
                                }
                            }
                    } else {
                    }
                } else {
                    println("No user found for email: $email")
                }
            } else {
                println("Failed to fetch sign-in methods for email: ${task.exception?.message}")
            }
        }*/
}
