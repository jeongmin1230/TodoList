package com.todo.todolist.screen.drawer

import com.todo.todolist.R

const val HOME = "home"
const val ADD = "add"
const val SETTING = "setting"
const val MY_PAGE = "myPage"

sealed class NavDrawer(
    val title: Int, val icon: Int, val screenRoute: String
) {
    object Home: NavDrawer(R.string.to_home, R.drawable.ic_home, HOME)
    object Add: NavDrawer(R.string.to_add, R.drawable.ic_add, ADD)
    object Setting: NavDrawer(R.string.setting, R.drawable.ic_setting, SETTING)
    object MyPage: NavDrawer(R.string.my_page, R.drawable.ic_my_page, MY_PAGE)
}