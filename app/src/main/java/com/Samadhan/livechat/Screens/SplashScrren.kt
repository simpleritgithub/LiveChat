package com.Samadhan.livechat.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.Samadhan.livechat.R
import com.Samadhan.livechat.data.DataStore
import com.Samadhan.livechat.navGraph.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val initialState = dataStore.getIpsaDataStoreVal("app_initial").collectAsState(initial = "")

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500L)
        isVisible = true
        delay(2500L)
        when(initialState.value) {
            "true" -> navController.navigate(Screen.ChatList.route)
            "login" -> navController.navigate(Screen.Login.route)
            else -> navController.navigate(Screen.SignUp.route)
        }
    }

    // Box to center the content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E88E5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animate the logo's opacity
            val logoAlpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f)

            Image(
                painter = painterResource(id = R.drawable.chat_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(alpha = logoAlpha) // Apply alpha for fade-in effect
            )

            Spacer(modifier = Modifier.height(16.dp))

            val textAlpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f)

            Text(
                text = "LiveChat",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.graphicsLayer(alpha = textAlpha)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
@Preview
fun SplashScreen(){
    SplashScreen(navController = rememberNavController())
}
