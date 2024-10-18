package com.Samadhan.livechat.data

data class UserData(
    val userId: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = ""
){
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl
    )
}

data class UserProfile(
    val name: String? = null,
    val number: String? = null,
    val imageUrl: String? = null
)

data class LoginData(
    val userId: String?
)


data class ChatData(
    val chatId:String?="",
    val user1:ChatUser = ChatUser(),
    val user2:ChatUser = ChatUser(),
)

data class ChatUser(
    val userId:String?="",
    val name:String?="",
    val imageUrl:String?="",
    val number:String?="",
)

data class Message(
    var sendBy:String? = "",
    val message:String?="",
    var timeStamp:String?=""
)