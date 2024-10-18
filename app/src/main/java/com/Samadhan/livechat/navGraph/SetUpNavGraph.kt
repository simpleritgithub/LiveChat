package com.Samadhan.livechat.navGraph

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.Samadhan.livechat.Screens.ChatListScreen
import com.Samadhan.livechat.Screens.LoginScreen
import com.Samadhan.livechat.Screens.ProfileScreen
import com.Samadhan.livechat.Screens.SignUpScreen
import com.Samadhan.livechat.Screens.SingleChatScreen
import com.Samadhan.livechat.Screens.SingleStatusScreen
import com.Samadhan.livechat.Screens.SplashScreen
import com.Samadhan.livechat.Screens.StatusListScreen

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun SetUpNavGraph(navController: NavHostController, isBottomMenuShow: MutableState<Boolean>) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(route = Screen.SplashScreen.route) {
            isBottomMenuShow.value = false
            SplashScreen(navController)
        }
        composable(route = Screen.SignUp.route) {
            isBottomMenuShow.value = false
            SignUpScreen(navController)
        }
        composable(route = Screen.Login.route) {
            isBottomMenuShow.value = false
            LoginScreen(navController)
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController,isBottomMenuShow)
        }
        composable(route = Screen.ChatList.route) {
            ChatListScreen(navController,isBottomMenuShow)
        }
        composable(route = Screen.StatusList.route) {
            StatusListScreen(navController,isBottomMenuShow)
        }
        composable(route = Screen.SingleStatus.route) {
            SingleStatusScreen()
        }
        composable(route = Screen.SingleChat.route) {
            SingleChatScreen(navController,isBottomMenuShow)
        }



    }
}