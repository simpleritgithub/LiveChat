package com.Samadhan.livechat.useCase

import android.annotation.SuppressLint
import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.core.text.isDigitsOnly
import com.Samadhan.livechat.data.ChatData
import com.Samadhan.livechat.data.ChatUser
import com.Samadhan.livechat.data.Constants.CHATS
import com.Samadhan.livechat.data.Constants.USER_NODE
import com.Samadhan.livechat.data.UserData
import com.Samadhan.livechat.data.UserProfile
import com.Samadhan.livechat.di.Resource
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val dataBase: FirebaseFirestore
) {
    @SuppressLint("SupportAnnotationUsage")
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend operator fun invoke(number: String, userId: String, userData: UserProfile?): Resource<Unit> {
        return try {
            if (number.isEmpty() || !number.isDigitsOnly()) {
                Log.e("ChatUseCase1", "invoke: ", )
                return Resource.Error("Invalid phone number")
            }
            Log.e("ChatUseCase2", "invoke: ", )
            val queryUser1 = dataBase.collection(CHATS)
                .whereEqualTo("user1.number", userData?.number)
                .whereEqualTo("user2.number", number)
            Log.e("ChatUseCase3", "invoke: ", )
            val querySnapshot = queryUser1.get().await()

            if (querySnapshot.isEmpty) {
                val queryUser2 = dataBase.collection(CHATS)
                    .whereEqualTo("user1.number", number)
                    .whereEqualTo("user2.number", userData?.number)

                val secondQuerySnapshot = queryUser2.get().await()
                if (secondQuerySnapshot.isEmpty) {
                    val userSnapshot = dataBase.collection(USER_NODE)
                        .whereEqualTo("number", number)
                        .get()
                        .await()

                    if (userSnapshot.isEmpty) {
                        return Resource.Error("Number not found")
                    } else {
                        val chatPartner = userSnapshot.toObjects(UserData::class.java).firstOrNull()
                        val chatId = dataBase.collection(CHATS).document().id
                        val chat = ChatData(
                            chatId = chatId,
                            user1 = ChatUser(
                                userId = userId,
                                name = userData?.name,
                                number = userData?.number,
                                imageUrl = userData?.imageUrl
                            ),
                            user2 = ChatUser(
                                userId = chatPartner?.userId,
                                name = chatPartner?.name,
                                number = chatPartner?.number,
                                imageUrl = chatPartner?.imageUrl
                            )
                        )
                        Log.e("ChatUseCase4", "invoke: ", )
                        dataBase.collection(CHATS).document(chatId).set(chat).await()
                        return Resource.Success(Unit)

                    }
                } else {
                    Log.e("ChatUseCase5", "invoke: ", )
                    return Resource.Error("Chat already exists")
                }
            } else {
                Log.e("ChatUseCase6", "invoke: ", )
                return Resource.Error("Chat already exists")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Incorrect email or password
            Resource.Error("Incorrect email or password")
        } catch (e: FirebaseAuthUserCollisionException) {
            // Handle case where email is already in use by another account
            Resource.Error("This email address is already in use by another account")
        } catch (e: FirebaseAuthException) {
            // Handle other Firebase authentication exceptions
            Resource.Error("Authentication failed: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }
}
