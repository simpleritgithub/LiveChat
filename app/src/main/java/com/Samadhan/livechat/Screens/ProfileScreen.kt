package com.Samadhan.livechat.Screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.Samadhan.livechat.data.DataStore
import com.Samadhan.livechat.navGraph.Screen
import com.Samadhan.livechat.viewModel.ChatViewModel
import com.Samadhan.livechat.viewModel.LCViewModel
import com.Samadhan.livechat.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun ProfileScreen(
    navController: NavController, isBottomMenuShow: MutableState<Boolean>,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    LCViewModel: LCViewModel = hiltViewModel()
) {
    isBottomMenuShow.value = true
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val uid = dataStore.getIpsaDataStoreVal("uid").collectAsState(initial = "")
    val data = profileViewModel.profileData.collectAsState().value
    if (uid.value.isNotEmpty() && uid.value.isNotBlank()) {
        profileViewModel.getProfileData(uid.value)
    }
    val scope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            it?.let {
                LCViewModel.uploadProfileImage(it)
            }
        }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Blue
                )
            },
            actions = {
                IconButton(onClick = {
                    chatViewModel.dePopulateMessage()
                    chatViewModel.currentChatMessageListener = null
                    navController.navigate(Screen.ChatList.route)
                }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Log Out")
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack(Screen.ChatList.route, false) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable {
//                        launcher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (data?.imageUrl != null) {
                    CommonImage(data = data.imageUrl, modifier = Modifier.size(120.dp))
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile Image",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data?.name ?: "Unknown",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Number: ${data?.number ?: "Unavailable"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        dataStore.clearIpsaDataStore()
                    }
                    profileViewModel.logOut()
                    chatViewModel.dePopulateMessage()
                    chatViewModel.currentChatMessageListener = null
                    navController.navigate(Screen.Login.route)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    text = "Log Out",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

}


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun ProfileImage(imageUrl: String?, viewModel: LCViewModel = hiltViewModel()) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            it?.let {
                viewModel.uploadProfileImage(it)
            }
        }
    Box(Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                onClick = { /*TODO*/ }, shape = CircleShape, modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {

            }
        }
    }
}

@Composable
fun CommonImage(
    data: String?, modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)
    androidx.compose.foundation.Image(painter = painter, contentDescription = null)

}