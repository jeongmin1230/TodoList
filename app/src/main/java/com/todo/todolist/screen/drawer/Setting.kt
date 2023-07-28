package com.todo.todolist.screen.drawer

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.todo.todolist.BuildConfig
import com.todo.todolist.R
import com.todo.todolist.screen.AppBar
import com.todo.todolist.screen.ConfirmDialog
import com.todo.todolist.screen.Login

@Composable
fun SettingScreen(drawerController: NavHostController) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    Column {
        AppBar(text = stringResource(id = R.string.setting)) { drawerController.popBackStack() }
        Text(
            text = stringResource(id = R.string.app_version),
            style = MaterialTheme.typography.bodyLarge.copy(Color.LightGray),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = BuildConfig.VERSION_NAME,
            style = MaterialTheme.typography.bodySmall.copy(Color.Black)
        )
        Divider(modifier = Modifier.border(BorderStroke(1.dp, Color.LightGray)))
        Text(
            text = stringResource(id = R.string.user_state),
            style = MaterialTheme.typography.bodyLarge.copy(Color.LightGray),
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = stringResource(id = R.string.logout),
            style = MaterialTheme.typography.bodySmall.copy(Color.Red),
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