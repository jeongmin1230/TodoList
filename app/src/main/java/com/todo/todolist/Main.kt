package com.todo.todolist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.todo.todolist.screen.drawer.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerNavController = rememberNavController()
    val navController = rememberNavController()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val items = listOf(
        NavDrawer.Home,
        NavDrawer.Add,
        NavDrawer.Setting
    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.ic_close),
                        modifier = Modifier
                            .padding(top = 4.dp, end = 4.dp)
                            .align(Alignment.End)
                            .clickable { scope.launch { drawerState.close() } }
                    )
                    Text(text = UserInfo.userName + stringResource(id = R.string.welcome),
                        style = MaterialTheme.typography.bodyLarge.copy(Color.Black),
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp, bottom = 10.dp))
                    Text(text = UserInfo.userEmail,
                        style = MaterialTheme.typography.bodySmall.copy(Color.DarkGray),
                        modifier = Modifier.padding(start = 10.dp, bottom = 30.dp))
                    Divider(Modifier.border(1.dp, Color.LightGray))
                    items.forEachIndexed { _, item ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    drawerNavController.navigate(item.screenRoute)
                                }
                                .padding(start = 10.dp, top = 12.dp, bottom = 12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(item.icon),
                                contentDescription = stringResource(id = item.title),
                                modifier = Modifier.padding(end = 10.dp))
                            Text(text = stringResource(id = item.title))
                        }
                    }
                }
            }
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                        .clickable { scope.launch { drawerState.open() } }
                )
                NavHost(drawerNavController, startDestination = NavDrawer.Home.screenRoute) {
                    composable(NavDrawer.Home.screenRoute) {
                        HomeScreen()
                    }
                    composable(NavDrawer.Add.screenRoute) {
                        AddScreen(navController)
                    }
                    composable(NavDrawer.Setting.screenRoute) {
                        SettingScreen()
                    }
                }
            }
        })
}