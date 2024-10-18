package com.Samadhan.livechat.useCase

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.Samadhan.livechat.data.Constants
import com.Samadhan.livechat.data.UserProfile
import com.Samadhan.livechat.di.Resource
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend operator fun invoke(userId: String): Resource<UserProfile> {
        return try {
            if (userId.isBlank()) {
                return Resource.Error("User ID cannot be empty")
            }
            val documentSnapshot = firestore.collection(Constants.USER_NODE).document(userId).get().await()
            if (documentSnapshot.exists()) {
                val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                if (userProfile != null) {
                    Resource.Success(userProfile)
                } else {
                    Resource.Error("Profile data is missing or malformed.")
                }
            } else {
                Resource.Error("User profile not found.")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Error("Incorrect email or password")
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("This email address is already in use by another account")
        } catch (e: FirebaseAuthException) {
            Resource.Error("Authentication failed: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }
}
