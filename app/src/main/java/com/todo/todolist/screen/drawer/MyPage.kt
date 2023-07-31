package com.todo.todolist.screen.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.todo.todolist.R
import com.todo.todolist.UserInfo
import com.todo.todolist.screen.AppBar

@Composable
fun MyPageScreen(drawerNavController: NavHostController) {
    Column(modifier = Modifier.padding(start = 10.dp)) {
       AppBar(text = stringResource(id = R.string.my_page)) { drawerNavController.popBackStack()}
        Spacer(modifier = Modifier.height(20.dp))
        Row(Modifier.padding(bottom = 10.dp)) {
            Text(
                text = stringResource(id = R.string.login_name),
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = UserInfo.userName,
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black)
            )
        }
        Row(Modifier.padding(bottom = 10.dp)) {
            Text(
                text = stringResource(id = R.string.login_email),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = UserInfo.userEmail,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}