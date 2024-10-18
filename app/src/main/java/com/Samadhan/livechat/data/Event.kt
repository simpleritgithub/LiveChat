package com.Samadhan.livechat.data

class Event {
}

sealed class UIEvent {
    data class ShowSnackbar(val message: String) : UIEvent()
    data object Loading : UIEvent()
    data object Success : UIEvent()
    data object UserAlreadyExist : UIEvent()
    data object Empty : UIEvent()
}