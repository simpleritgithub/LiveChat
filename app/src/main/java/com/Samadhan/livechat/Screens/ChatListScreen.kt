package com.Samadhan.livechat.Screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.Samadhan.livechat.CircularProgressBar
import com.Samadhan.livechat.CommonRow
import com.Samadhan.livechat.data.ChatData
import com.Samadhan.livechat.data.ChatUser
import com.Samadhan.livechat.data.Constants
import com.Samadhan.livechat.data.DataStore
import com.Samadhan.livechat.data.UIEvent
import com.Samadhan.livechat.navGraph.Screen
import com.Samadhan.livechat.ui.theme.light_message_sent
import com.Samadhan.livechat.viewModel.ChatViewModel
import com.Samadhan.livechat.viewModel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun ChatListScreen(
    navController: NavController, isBottomMenuShow: MutableState<Boolean>,
    chatViewModel: ChatViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    isBottomMenuShow.value = true
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val initial = dataStore.getIpsaDataStoreVal("uid").collectAsState(initial = "")
    BackHandler {

    }
    LaunchedEffect(Unit) {
        dataStore.setIpsaDataStoreVal("app_initial","true")
    }
    if (initial.value.isNotEmpty()){
        LaunchedEffect(key1 = true) {
            profileViewModel.getProfileData(initial.value)
        }
    }
    val callChatService = remember {
        mutableStateOf(false)
    }
    val chat = chatViewModel.chat.value
    val userData = profileViewModel.profileData.collectAsState().value
    val showDialog = remember {
        mutableStateOf(false)
    }
    val onFabClick: () -> Unit = { showDialog.value = true }
    val onDismiss: () -> Unit = { showDialog.value = false }
    val onAddChat: (String) -> Unit = {
        chatViewModel.onAddChat(it,initial.value,userData)
        showDialog.value = false
    }
    val isLoading = remember {
        mutableStateOf(false)
    }
    if (initial.value.isNotEmpty() && callChatService.value) {
        LaunchedEffect(key1 = true) {
            chatViewModel.populateChats(initial.value)
        }
    }

    LaunchedEffect(key1 = true) {
        profileViewModel.profileState.collectLatest { event->
            when(event){
                is UIEvent.Success ->{
                    isLoading.value = false
                    callChatService.value = true
                }
                is UIEvent.ShowSnackbar ->{

                }
                is UIEvent.Loading ->{
                    isLoading.value = true
                }
                else ->{

                }
            }

        }
    }
    LaunchedEffect(key1 = true) {
        chatViewModel.chatState.collectLatest { event->
            when(event){
                is UIEvent.Success ->{
                    callChatService.value = true
                    isLoading.value = false
                }
                is UIEvent.ShowSnackbar ->{

                }
                is UIEvent.Loading ->{
                    isLoading.value = true
                }
                else ->{

                }
            }

        }
    }
    BackHandler {
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Blue
                    )
                },

            )
        },
        floatingActionButton = {
            ShowDialog(
                showDialog = showDialog.value,
                onFabClick = onFabClick,
                onDismiss = onDismiss,
                onAddChat = onAddChat
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(light_message_sent)
                .padding(top = 100.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (chat.isNotEmpty()) {
                Constants.chat = chat
                LazyColumn(modifier = Modifier.weight(1f),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.Top) {
                    items(chat) { chatItem ->
                        val chatUser = if (chatItem.user1.userId == initial.value) {
                            chatItem.user2
                        } else {
                            chatItem.user1
                        }
                        ChatRow(imageUrl = chatUser.imageUrl, name = chatUser.name ?: "Unknown") {
                            chatItem.chatId?.let {
                                Constants.tempData["chatId"] = chatItem.chatId
                                Constants.tempData["userId"]= initial.value
                                if (initial.value.isNotEmpty()){
                                    navController.navigate(Screen.SingleChat.route)

                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No chats available.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Gray
                )
            }
            if (isLoading.value) {
                CircularProgressBar(isLoading.value)
            }
        }
    }

}

@Composable
fun ChatRow(imageUrl: String?, name: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick.invoke()
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "User Image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape) // Circular image
                    .border(2.dp, Color(0xFF1E88E5), CircleShape) // Border around image
            )}
            else{
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF1E88E5), CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = name ?: "Unknown User",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ShowDialog(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit,
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = { onDismiss.invoke() }, confirmButton = {
            Button(onClick = { onAddChat(addChatNumber.value) }) {
                Text(text = "Add Chat")
            }
        },
            title = {
                Text(text = "Add Chat")
            },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value, onValueChange = {
                        addChatNumber.value = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    }
    FloatingActionButton(
        onClick = { onFabClick.invoke() },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 80.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
    }
}