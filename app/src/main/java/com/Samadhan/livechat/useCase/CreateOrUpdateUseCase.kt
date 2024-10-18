package com.Samadhan.livechat.useCase

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.Samadhan.livechat.data.Constants
import com.Samadhan.livechat.data.UserData
import com.Samadhan.livechat.di.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class CreateOrUpdateUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataBase: FirebaseFirestore
) {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend operator fun invoke(
        name: String?,
        number: String?,
        imageUrl: String?
    ): Resource<UserData> {
        val uid = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")
        val userData = UserData(
            userId = uid,
            name = name,
            number = number,
            imageUrl = imageUrl
        )

        return try {
            Resource.Loading<Unit>()
            val result = dataBase.collection(Constants.USER_NODE).document(uid).get().await()

            if (result.exists()) {
                Constants.tempData["uid"] = auth.currentUser?.uid ?: ""
                updateUser(uid, userData)
                Resource.Success(userData)
            } else {
                createUser(uid, userData)
                Resource.Success(userData)
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

    private suspend fun updateUser(uid: String, userData: UserData): Resource<Unit> {
        return try {
            dataBase.collection(Constants.USER_NODE).document(uid).set(userData).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred while updating user")
        }
    }

    private suspend fun createUser(uid: String, userData: UserData): Resource<Unit> {
        return try {
            dataBase.collection(Constants.USER_NODE).document(uid).set(userData).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred while creating user")
        }
    }

}