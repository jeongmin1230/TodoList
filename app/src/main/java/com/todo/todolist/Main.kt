package com.todo.todolist

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val mainString = stringArrayResource(id = R.array.main_string)
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = mainString[0]) {
        composable(mainString[0]){
            Column {

            }
        }
    }
}

@Composable
fun WriteToDo() {
    Column {

    }
}