package com.todo.todolist.screen.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.todo.todolist.R

@Composable
fun AddScreen(navController: NavHostController) {
    var todo by remember { mutableStateOf("") }
    Column {
        TextField(
            value = todo,
            singleLine = true,
            onValueChange = {todo = it},
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        )

        Button(onClick = {
            navController.popBackStack()
            addTodo(todo.trim())
        }) {
            Text(
                text = stringResource(R.string.add_todo),
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
            )
        }
    }
}
@Composable
fun WriteTodoDialog(open: MutableState<Boolean>, navController: NavHostController) {
    var todo by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { open.value = false },
        title = { Text(text = stringResource(id = R.string.enter_todo)) },
        text = {
            TextField(
                value = todo,
                singleLine = true,
                onValueChange = {todo = it},
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton =
        {
            Text(text = stringResource(R.string.add_todo),
                modifier = Modifier
                    .clickable {
                        open.value = false
                        navController.popBackStack()
                        addTodo(todo.trim())
                    }
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),

        )
}

private fun addTodo(todo: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("todo")
    val todoRef = usersRef.child(uid.toString()).child("todo")
    val todoId = todoRef.push().key
    val completeRef = usersRef.child(uid.toString()).child("complete")
    completeRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (childSnapshot in dataSnapshot.children) {
                val value = childSnapshot.getValue(String::class.java)
                if (value == todo) {
                    childSnapshot.ref.removeValue()
                    break
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // 에러 처리 로직
        }
    })

    if (todoId != null) {
        val newTodoRef = todoRef.child(todoId)
        newTodoRef.setValue(todo)
    }
}