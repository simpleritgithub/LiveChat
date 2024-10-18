package com.Samadhan.livechat.navGraph

sealed class Screen(var route:String) {
    object SignUp : Screen("singUp")
    object SplashScreen : Screen("splashScreen")
    object Login : Screen("login")
    object Profile : Screen("profile")
    object ChatList : Screen("chatList")
    object SingleChat : Screen("singleChat")
    object StatusList : Screen("statusList")
    object SingleStatus : Screen("singleStatus")
}