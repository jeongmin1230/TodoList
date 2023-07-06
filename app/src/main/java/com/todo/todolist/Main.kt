package com.todo.todolist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
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
                MainList()
            }
        }
    }
}

@Composable
fun MainList() {
    var isMenuOpen by remember { mutableStateOf(false) }

    val writeOpenDialog = remember { mutableStateOf(false) }
    val mainString = stringArrayResource(id = R.array.main_string)
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
                contentDescription = stringResource(id = R.string.ic_menu),
                modifier = Modifier
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { isMenuOpen = !isMenuOpen }
                    .padding(horizontal = 12.dp)
                )
            Button(onClick = {writeOpenDialog.value = !writeOpenDialog.value},
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)) {
                Text(text = mainString[0])
            }
        }
    }
    Row { // 몬가 이상
        if(isMenuOpen) {
            Column {
                Text(text = "로그아웃")
            }
        }
        TodoListScreen()
    }
    if(writeOpenDialog.value) WriteTodoDialog(writeOpenDialog, mainString)
}

@Composable
fun WriteTodoDialog(open: MutableState<Boolean>, mainString: Array<String>) {
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
        ListName(stringResource(id = R.string.main_todo_list))
        todoListState.value.forEach { todo ->
            EachList(todo, true, ImageVector.vectorResource(id = R.drawable.ic_uncheck)) {
                doneTodo(todo)
            }
        }
        ListName(stringResource(id = R.string.main_done_list))
        doneTodoListState.value.forEach { done ->
            EachList(done, false, ImageVector.vectorResource(id = R.drawable.ic_check)) {
                cancelDone(done)
            }
        }
    }
}

@Composable
fun ListName(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EachList(eachName:String, type: Boolean, image: ImageVector, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(all = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Color.LightGray)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) { onClick() }) {
            Image(imageVector = image,
                contentDescription = stringResource(id = R.string.check_state),
                modifier = Modifier.padding(horizontal = 8.dp))
            Text(text = eachName,
                style = TextStyle(textDecoration = if(type) TextDecoration.None else TextDecoration.LineThrough),)
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

private fun cancelDone(todo: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("todo")
    val completeRef = usersRef.child(uid.toString()).child("complete")
    val todoRef = usersRef.child(uid.toString()).child("todo")
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

    // completeRef에 해당 todo 추가
    val todoId = todoRef.push().key
    if (todoId != null) {
        val newCompleteRef = todoRef.child(todoId)
        newCompleteRef.setValue(todo)
    }
}
