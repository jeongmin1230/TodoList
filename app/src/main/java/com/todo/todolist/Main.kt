package com.todo.todolist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "first") {
        composable("first"){
            Column {
                MainList(navController)
            }
        }
    }
}

@Composable
fun MainList(navController: NavHostController) {
    val writeOpenDialog = remember { mutableStateOf(false) }
    val mainString = stringArrayResource(id = R.array.main_string)
    Column {
        Button(onClick = {writeOpenDialog.value = !writeOpenDialog.value},
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()) {
            Text(text = mainString[0])
        }
    }
    if(writeOpenDialog.value) WriteTodoDialog(writeOpenDialog, mainString, navController)
}

@Composable
fun EachTodoLayout() {

}

@Composable
fun WriteTodoDialog(open: MutableState<Boolean>, mainString: Array<String>, navController: NavHostController) {
    var title by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { open.value = false },
            title = { Text(text = mainString[1])},
            text = {
                TextField(
                    value = title,
                    singleLine = true,
                    onValueChange = {title = it},
                    placeholder = {
                        Text(text = stringResource(id = R.string.email),
                            style = MaterialTheme.typography.bodyMedium)
                    },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                )
            },
            confirmButton =
            {
                Text(text = mainString[2],
                    modifier = Modifier
                        .clickable { /* TODO 파이어베이스 데이터베이스에 추가 */ }
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),

        )
}