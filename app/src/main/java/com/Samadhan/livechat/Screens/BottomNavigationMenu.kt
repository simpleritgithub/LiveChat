package com.Samadhan.livechat.Screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.Samadhan.livechat.navGraph.Screen

@Composable
fun BottomNavigationMenu(navController: NavHostController) {
    val selectedItem = remember { mutableStateOf(0) }

    val items = listOf("Home", "Search", "Profile")

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (item) {
                            "Home" -> Icons.Default.Home
                            "Search" -> Icons.Default.Search
                            "Profile" -> Icons.Default.Person
                            else -> Icons.Default.Home
                        },
                        contentDescription = null
                    )
                },
                label = { Text(item) },
                selected = selectedItem.value == index,
                onClick = {
                    if (selectedItem.value != index) {
                        selectedItem.value = index
                        when (index) {
                            0 -> {
                                navController.navigate(Screen.ChatList.route) {
                                    popUpTo(Screen.ChatList.route) { inclusive = true }
                                }
                            }
                            1 -> {
                                navController.navigate(Screen.StatusList.route) {
                                    popUpTo(Screen.StatusList.route) { inclusive = true }
                                }
                            }
                            2 -> {
                                navController.navigate(Screen.Profile.route) {
                                    popUpTo(Screen.Profile.route) { inclusive = true }
                                }
                            }
                        }
                        }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Blue,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.Blue,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
@Preview
fun BottomNavigationMenuPreview(){
    BottomNavigationMenu(rememberNavController())
}