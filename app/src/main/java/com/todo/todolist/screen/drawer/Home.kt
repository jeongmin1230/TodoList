package com.todo.todolist.screen.drawer

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.todo.todolist.R
import com.todo.todolist.UserInfo
import com.todo.todolist.screen.ConfirmDialog
import com.todo.todolist.screen.getStoredUserEmail
import com.todo.todolist.screen.getStoredUserPassword

enum class BounceState { Pressed, Released }

@Composable
fun HomeScreen(clickAction: () -> Unit) {
    val context = LocalContext.current
    UserInfo.userEmail = getStoredUserEmail(context)
    UserInfo.userPassword = getStoredUserPassword(context)
    val todoListState = remember { mutableStateOf(emptyList<String>()) }
    val doneTodoListState = remember { mutableStateOf(emptyList<String>()) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("todo")
    val todoRef = usersRef.child(uid.toString()).child("todo")
    val doneTodoRef = usersRef.child(uid.toString()).child("complete")

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_list))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

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

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .clickable { clickAction() }
        )
        Column(Modifier.padding(top = 10.dp),) {
            if(todoListState.value.isEmpty() && doneTodoListState.value.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.size(350.dp)) {
                        LottieAnimation(
                            composition = composition,
                            progress = progress,
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.empty_list),
                        style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray)
                    )
                }
            }
            else {
                if(todoListState.value.isNotEmpty()) {
                    ListName(stringResource(id = R.string.main_todo_list))
                    todoListState.value.forEach { todo ->
                        EachList(false, todo, true, ImageVector.vectorResource(id = R.drawable.ic_uncheck))
                    }
                }
                if(doneTodoListState.value.isNotEmpty()) {
                    ListName(stringResource(id = R.string.main_done_list))
                    doneTodoListState.value.forEach { done ->
                        EachList(true, done, false, ImageVector.vectorResource(id = R.drawable.ic_check))
                    }
                }
            }
        }
    }
}


private fun doneTodo(todo: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("todo")
    val todoRef = usersRef.child(uid.toString()).child("todo")
    val completeRef = usersRef.child(uid.toString()).child("complete")
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

    val completeId = completeRef.push().key
    if (completeId != null) {
        val newCompleteRef = completeRef.child(completeId)
        newCompleteRef.setValue(todo)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EachList(isDone: Boolean, eachName:String, type: Boolean, image: ImageVector) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    val currentState: BounceState by remember { mutableStateOf(BounceState.Released) }
    val transition = updateTransition(targetState = currentState, label = "animation")
    val scale: Float by transition.animateFloat(
        transitionSpec = { spring(stiffness = 900f) }, label = ""
    ) { state ->
        if (state == BounceState.Pressed) {
            0.95f
        } else {
            1f
        }
    }
    Column(modifier = Modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .combinedClickable(
            onClick = {
                if (!isDone) doneTodo(eachName)
                else cancelDone(eachName)
            },
            onLongClick = { showConfirmDialog = true }
        )
        .padding(all = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Color.LightGray)
                .fillMaxWidth()
                .padding(all = 8.dp)) {
            Image(imageVector = image,
                contentDescription = stringResource(id = R.string.check_state))
            Text(text = eachName,
                style = TextStyle(textDecoration = if(type) TextDecoration.None else TextDecoration.LineThrough),
                modifier = Modifier.padding(start = 8.dp))
        }
    }
    if(showConfirmDialog) {
        ConfirmDialog(
            onDismiss = { showConfirmDialog = false },
            content = stringResource(id = R.string.do_delete),
            confirmAction = {
                showConfirmDialog = false
                deleteTodo(eachName) }) {
            showConfirmDialog = false
        }
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

private fun deleteTodo(todo: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("todo")
    val todoRef = usersRef.child(uid.toString()).child("todo")
    val completeRef = usersRef.child(uid.toString()).child("complete")

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
}