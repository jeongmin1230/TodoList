package com.todo.todolist.screen.drawer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.todo.todolist.R
import com.todo.todolist.screen.AppBar
import com.todo.todolist.screen.Mean
import com.todo.todolist.screen.TextFieldPlaceholder

@SuppressLint("ResourceType")
@Composable
fun AddScreen(drawerNavController: NavHostController) {
    val context = LocalContext.current
    val todo = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf("") }
    var columnSize by remember { mutableStateOf(Size.Zero) }
    val classificationArray = stringArrayResource(id = R.array.classification_type)
    val dropDownVisible = remember { mutableStateOf(false) }
    val classification = remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate.value = "${year}년 ${month+1}월 ${dayOfMonth}일"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column {
        AppBar(text = stringResource(id = R.string.add_todo)) { drawerNavController.popBackStack() }
        Column(Modifier.padding(horizontal = 10.dp)) {
            Mean(stringResource(id = R.string.todo))
            TextField(
                value = todo.value,
                singleLine = true,
                onValueChange = { todo.value = it },
                placeholder = { TextFieldPlaceholder(stringResource(id = R.string.enter_todo)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Mean(stringResource(id = R.string.deadline))
            Column(modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(start = 14.dp, top = 20.dp, bottom = 20.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) { datePickerDialog.show() }) {
                if (selectedDate.value.isEmpty()) {
                    TextFieldPlaceholder(stringResource(id = R.string.enter_deadline))
                } else {
                    Text(
                        text = selectedDate.value,
                        style = MaterialTheme.typography.bodySmall.copy(Color.Black)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Mean(stringResource(id = R.string.classification))
            Column(modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(all = 16.dp)
                .clickable(interactionSource = MutableInteractionSource(), indication = null) { dropDownVisible.value = true }) {
                Box {
                    Row{
                        Column(Modifier.weight(1f)) {
                            classification.value.ifEmpty { TextFieldPlaceholder(stringResource(id = R.string.enter_classification)) }
                        }
                        Image(
                            imageVector =
                            if (dropDownVisible.value) ImageVector.vectorResource(id = R.drawable.ic_dropdown_up)
                            else ImageVector.vectorResource(id = R.drawable.ic_dropdown_down),
                            contentDescription = stringResource(id = R.string.is_expanded)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    drawerNavController.popBackStack()
                    addTodo(todo.value.trim())
                },
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