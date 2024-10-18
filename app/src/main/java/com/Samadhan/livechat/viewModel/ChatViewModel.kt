package com.Samadhan.livechat.viewModel

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Samadhan.livechat.data.ChatData
import com.Samadhan.livechat.data.Constants.CHATS
import com.Samadhan.livechat.data.Constants.MESSAGE
import com.Samadhan.livechat.data.Message
import com.Samadhan.livechat.data.UIEvent
import com.Samadhan.livechat.data.UserProfile
import com.Samadhan.livechat.di.Resource
import com.Samadhan.livechat.useCase.ChatUseCase
import com.Samadhan.livechat.useCase.ProfileUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val dataBase: FirebaseFirestore,
    private val chatUseCase: ChatUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<UIEvent>(UIEvent.Empty)
    val profileState: StateFlow<UIEvent> = _profileState

    val chat = mutableStateOf<List<ChatData>>(listOf())

    private val _chatState = MutableStateFlow<UIEvent>(UIEvent.Empty)
    val chatState: StateFlow<UIEvent> = _chatState
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    var currentChatMessageListener: ListenerRegistration? = null

    fun populateMessages(chatId: String){
        currentChatMessageListener = dataBase.collection(CHATS).document(chatId).collection(MESSAGE).addSnapshotListener { value, error ->
            if (error != null){
                Log.e("TAG", "populateMessages: Error", )
            }

            if (value != null) {
                chatMessages.value = value.documents.mapNotNull { document ->
                    document.toObject(Message::class.java) // Ensure the correct class is used
                }.sortedBy { message ->
                    message.timeStamp // Ensure safe access to timeStamp
                }
            }

        }
    }

    fun dePopulateMessage(){
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }
    fun onAddChat(number: String, userId: String, userData: UserProfile?) {
        viewModelScope.launch {
            _chatState.value = UIEvent.Loading

            val result = chatUseCase(number, userId, userData)

            when (result) {
                is Resource.Success -> {
                    Log.e("ChatUseCase8", "invoke: ")
                    _chatState.value = UIEvent.Success
                }

                is Resource.Error -> {
                    Log.e("ChatUseCase9", "invoke: ")
                    _chatState.value = UIEvent.ShowSnackbar(result.message ?: "Error occurred")
                }

                is Resource.Loading -> {
                    Log.e("ChatUseCase10", "invoke: ")
                    _chatState.value = UIEvent.Loading
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun populateChats(userId: String) {
        if (userId.isEmpty()) {
            Log.e("TAG", "populateChats: User ID is empty")
            return
        }
        val queryUser1 = dataBase.collection(CHATS)
            .whereEqualTo("user1.userId", userId)

        val queryUser2 = dataBase.collection(CHATS)
            .whereEqualTo("user2.userId", userId)
        queryUser1.addSnapshotListener { querySnapshot1, error1 ->
            if (error1 != null) {
                Log.e("TAG", "Error fetching chats for user1: ", error1)
                return@addSnapshotListener
            }
            queryUser2.addSnapshotListener { querySnapshot2, error2 ->
                if (error2 != null) {
                    Log.e("TAG", "Error fetching chats for user2: ", error2)
                    return@addSnapshotListener
                }
                val chatsUser1 = querySnapshot1?.toObjects(ChatData::class.java) ?: emptyList()
                val chatsUser2 = querySnapshot2?.toObjects(ChatData::class.java) ?: emptyList()

                val allChats = chatsUser1 + chatsUser2
                chat.value = allChats
            }
        }
    }

    fun onSendReplay(chatId: String, message: String, userId: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = com.Samadhan.livechat.data.Message(userId, message, time)
        dataBase.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }

}