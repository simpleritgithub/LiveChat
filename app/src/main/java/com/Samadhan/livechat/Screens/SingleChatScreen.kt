package com.Samadhan.livechat.Screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.Samadhan.livechat.data.Constants
import com.Samadhan.livechat.data.Message
import com.Samadhan.livechat.navGraph.Screen
import com.Samadhan.livechat.ui.theme.light_message_sent
import com.Samadhan.livechat.viewModel.ChatViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun SingleChatScreen(navController: NavController,
                     isBottomMenuShow: MutableState<Boolean>, chatViewModel: ChatViewModel = hiltViewModel()) {
    isBottomMenuShow.value = false
    var replay by rememberSaveable {
        mutableStateOf("")
    }
    val chatId = Constants.tempData["chatId"]
    val userId = Constants.tempData["userId"]
    val onSendReplay = {
        chatViewModel.onSendReplay(
            chatId = chatId?:"",
            message = replay,
            userId = userId?:""
        )
        replay = ""
    }
    val currentChat = Constants.chat.first{
        it.chatId == chatId
    }
    val chatMessages = chatViewModel.chatMessages.value
    val chaUser = if (userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1
    Log.e("TAG", "SingleChatScreen: ${chaUser.name}", )
    LaunchedEffect(key1 = Unit) {
        if (chatId != null) {
            chatViewModel.populateMessages(chatId)
        }
    }
    BackHandler {
        chatViewModel.dePopulateMessage()
        navController.popBackStack(Screen.ChatList.route,false)
    }
    Scaffold(
        topBar = {
            ChatHeader(name = chaUser.name?:"Unknown", imageUrl = chaUser.imageUrl) {
                chatViewModel.dePopulateMessage()
                navController.popBackStack(Screen.ChatList.route,false)
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxSize().background(light_message_sent)
                .imePadding() ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp)
            ) {
                Spacer(modifier = Modifier.height(70.dp))
                ChatMessages(modifier = Modifier.weight(1f), chatMessages = chatMessages, currentUserId = userId ?: "")
            }
            ReplayBlock(
                replay = replay,
                onReplayChange = { replay = it },
                onSendReplay = onSendReplay,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessages(
    modifier: Modifier,
    chatMessages: List<Message>,
    currentUserId: String
) {
    val listState = rememberLazyListState()
    val keyboardVisible = WindowInsets.isImeVisible
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1) // Scroll to the bottom (latest message)
        }
    }
    LaunchedEffect(keyboardVisible) {
        if (keyboardVisible)
        listState.animateScrollToItem(chatMessages.size - 1)
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.Top,
    ) {
        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) Color(0xFF4BC438) else Color(0xFFC0C0C0)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp),
                    color = Color.White
                )
            }
        }
    }
}



@Composable
fun ChatHeader(
    name: String,
    imageUrl: String?,
    onBackClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth().background(Color.White)
                .padding(top = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null,
                modifier = Modifier
                    .clickable { onBackClick.invoke() }
                    .padding(8.dp),
                tint = Color.Black)

            if (imageUrl != null) {
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Black
            )
        }
        Divider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplayBlock(
    replay: String,
    onReplayChange: (String) -> Unit,
    onSendReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Divider(Modifier.size(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .imePadding() ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = replay,
                onValueChange = onReplayChange,
                maxLines = 3,
                placeholder = {
                    Text(text = "Type your message...", color = Color.Gray) // Placeholder text
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Blue
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            )

            Button(onClick = { onSendReplay.invoke() }) {
                Text(text = "Send")
            }
        }
    }
}
