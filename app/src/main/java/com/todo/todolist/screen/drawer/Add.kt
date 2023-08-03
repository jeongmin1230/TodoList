package com.todo.todolist.screen.drawer

import androidx.compose.foundation.layout.*
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
import com.todo.todolist.screen.AppBar
import com.todo.todolist.screen.TextFieldForm
import com.todo.todolist.screen.TextFieldPlaceholder

@Composable
fun AddScreen(drawerNavController: NavHostController) {
    val todo = remember { mutableStateOf("") }
    val deadline = remember { mutableStateOf("") }
    val classification = remember { mutableStateOf("") }
    Column {
        AppBar(text = stringResource(id = R.string.add_todo)) { drawerNavController.popBackStack() }
        Column(Modifier.padding(horizontal = 10.dp)) {
            TextFieldForm(stringResource(id = R.string.todo), todo, stringResource(id = R.string.enter_todo))
            TextFieldForm(stringResource(id = R.string.deadline), deadline, stringResource(id = R.string.enter_deadline))
            TextFieldForm(stringResource(id = R.string.classification), classification, stringResource(id = R.string.enter_classification))
            Button(
                onClick = {
                drawerNavController.popBackStack()
                addTodo(todo.value.trim())
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.add_todo),
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                )
            }
        }

    }
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