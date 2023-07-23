package com.todo.todolist.screen.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todo.todolist.R
import com.todo.todolist.UserInfo

@Composable
fun SettingScreen() {
    Column {
        Text(text = stringResource(id = R.string.user_state),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray))
        Row {
            Text(text = stringResource(id = R.string.login_name),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(text = UserInfo.userName,
                style = MaterialTheme.typography.bodyMedium)
        }
        Row {
            Text(text = stringResource(id = R.string.login_email),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(text = UserInfo.userEmail,
                style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = stringResource(id = R.string.logout),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable { /* 로그 아웃*/ }) // 로그 아웃 하겠냐는 다이얼 로그도 나오게!
    }
}