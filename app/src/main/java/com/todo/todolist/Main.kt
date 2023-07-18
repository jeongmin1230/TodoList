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
import com.todo.todolist.screen.drawer.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerNavController = rememberNavController()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val items = listOf(
        NavDrawer.Home,
        NavDrawer.Add,
        NavDrawer.Setting
    )
    ModalNavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)) {
                // TODO 로그인 한 사람 이름
                Text(text = "이정민 ")
            }
            Column(Modifier.padding(horizontal = 12.dp)) {
                items.forEachIndexed { _, navDrawer ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                drawerNavController.navigate(navDrawer.screenRoute)
                            }) {
                        Image(
                            imageVector = ImageVector.vectorResource(navDrawer.icon),
                            contentDescription = stringResource(id = navDrawer.title))
                        Text(text = stringResource(id = navDrawer.title))
                    }
                }
            }
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        println("open drawer")
                        scope.launch { drawerState.open() } }
                )
                NavHost(navController = drawerNavController, startDestination = NavDrawer.Home.screenRoute) {
                    composable(NavDrawer.Home.screenRoute){
                        Column {
                            HomeScreen()
                        }
                    }
                    composable(NavDrawer.Add.screenRoute) {
                        Column {
                            AddScreen(drawerNavController)
                        }
                    }
                    composable(NavDrawer.Setting.screenRoute) {
                        Column {
                            SettingScreen()
                        }
                    }
                }
            }
        }
    )
}