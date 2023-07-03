package com.todo.todolist

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        TodoListScreen()
    }
    if(writeOpenDialog.value) WriteTodoDialog(writeOpenDialog, mainString, navController)
}

@Composable
fun EachTodoLayout(todo: String, onTodoClicked: (String) -> Unit) {
    var done by remember { mutableStateOf(false) }
    val textDecoration = if (done) {
        TextStyle(textDecoration = TextDecoration.LineThrough)
    } else {
        TextStyle(textDecoration = TextDecoration.None)
    }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(1.dp, Color.LightGray)
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                done = !done
                if (done) doneTodo(todo)
                else addTodo(todo)
            }) {
        Image(imageVector =
                if(done) ImageVector.vectorResource(id = R.drawable.ic_check)
                else ImageVector.vectorResource(id = R.drawable.ic_uncheck),
            contentDescription = null)
        Text(text = todo,
            style = textDecoration,
            modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun WriteTodoDialog(open: MutableState<Boolean>, mainString: Array<String>, navController: NavHostController) {
    var todo by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { open.value = false },
            title = { Text(text = mainString[1])},
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
                Text(text = mainString[2],
                    modifier = Modifier
                        .clickable {
                            open.value = false
                            addTodo(todo)
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

@Composable
fun TodoListScreen() {
    val todoListState = remember { mutableStateOf(emptyList<String>()) }
    val doneTodoListState = remember { mutableStateOf(emptyList<String>()) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("todo")
    val todoRef = usersRef.child(uid.toString()).child("todo")
    val doneTodoRef = usersRef.child(uid.toString()).child("complete")

    // 데이터 변경 이벤트 리스너 등록
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val todoList = mutableListOf<String>()
            for (childSnapshot in dataSnapshot.children) {
                val todoText = childSnapshot.getValue(String::class.java)
                if (todoText != null) {
                    todoList.add(todoText)
                }
            }
            todoListState.value = todoList
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // 에러 처리 로직
        }
    }
    val doneEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val doneTodoList = mutableListOf<String>()
            for (childSnapshot in dataSnapshot.children) {
                val todoText = childSnapshot.getValue(String::class.java)
                if (todoText != null) {
                    doneTodoList.add(todoText)
                }
            }
            doneTodoListState.value = doneTodoList
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // 에러 처리 로직
        }
    }

    todoRef.addValueEventListener(valueEventListener)
    doneTodoRef.addValueEventListener(doneEventListener)

    Column {
        todoListState.value.forEach { todo ->
            Column(modifier = Modifier.padding(all = 10.dp)) {
                EachTodoLayout(todo) {
                    println(it)
                }
            }
        }
        Spacer(modifier = Modifier.border(1.dp, Color.LightGray))
        doneTodoListState.value.forEach { done ->
            Column(modifier = Modifier.padding(all = 10.dp)) {
                EachTodoLayout(done) {
                    println(it)
                }
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

private fun doneTodo(todo: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("todo")
    val todoRef = usersRef.child(uid.toString()).child("todo")
    val completeRef = usersRef.child(uid.toString()).child("complete")
    // todoRef에서 해당 todo 제거
    todoRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

    // completeRef에 해당 todo 추가
    val completeId = completeRef.push().key
    if (completeId != null) {
        val newCompleteRef = completeRef.child(completeId)
        newCompleteRef.setValue(todo)
    }
}
