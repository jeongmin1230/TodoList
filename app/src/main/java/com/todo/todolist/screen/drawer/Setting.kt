package com.todo.todolist.screen.drawer

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.todo.todolist.R
import com.todo.todolist.UserInfo
import com.todo.todolist.screen.ConfirmDialog
import com.todo.todolist.screen.Login

@Composable
fun SettingScreen() {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    Column {
        Text(text = stringResource(id = R.string.setting),
            style = MaterialTheme.typography.bodyMedium.copy(Color.Black))
        Text(
            text = stringResource(id = R.string.user_state),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        )
        Row(Modifier.padding(bottom = 10.dp)) {
            Text(
                text = stringResource(id = R.string.login_name),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = UserInfo.userName,
                style = MaterialTheme.typography.bodyMedium
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
        Text(text = stringResource(id = R.string.logout),
            style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
            modifier = Modifier.clickable { showConfirmDialog = true })
    }

    if(showConfirmDialog) {
        ConfirmDialog(
            onDismiss = {showConfirmDialog = false},
            content = stringResource(id = R.string.do_logout),
            confirmAction = {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                storeUserCredentials(context)
                context.startActivity(intent)
                Toast.makeText(context, context.getString(R.string.logout_apply), Toast.LENGTH_SHORT).show()
            },
            dismissAction = {showConfirmDialog = false}
        )
    }
}

private fun storeUserCredentials(context: Context) {
    val sharedPreferences = context.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("email")
    editor.remove("password")
    editor.apply()
}