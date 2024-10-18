package com.Samadhan.livechat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.Samadhan.livechat.Screens.BottomNavigationMenu
import com.Samadhan.livechat.navGraph.SetUpNavGraph
import com.Samadhan.livechat.ui.theme.LiveChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiveChatTheme {
                val isBottomMenuShow = remember {
                    mutableStateOf(false)
                }
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isBottomMenuShow.value){
                            BottomNavigationMenu(navController)
                        }
                    }) { innerPadding ->
                    SetUpNavGraph(navController = navController,isBottomMenuShow)
                }
            }
        }
    }
    
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LiveChatTheme {
        Greeting("Android")
    }
}