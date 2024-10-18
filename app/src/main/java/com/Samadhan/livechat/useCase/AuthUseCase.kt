package com.Samadhan.livechat.useCase

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.Samadhan.livechat.data.Constants
import com.Samadhan.livechat.data.LoginData
import com.Samadhan.livechat.di.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val auth: FirebaseAuth
) {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend operator fun invoke(email: String, password: String): Resource<Unit> {
        return try {
            Resource.Loading<Unit>()
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            if (result.user != null) {
                Resource.Success(Unit)
            } else {
                Resource.Error("User creation failed")
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("This email is already registered. Please try logging in.")

        } catch (e: FirebaseAuthException) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Resource.Error("Invalid email or password. Please ensure your email is correct and your password is strong.")
                }
                is FirebaseAuthInvalidUserException -> {
                    Resource.Error("Invalid user. Please check your details and try again.")
                }
                else -> {
                    Resource.Error("Authentication error: ${e.localizedMessage ?: "Unknown error occurred"}")
                }
            }

        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "A network error occurred. Please try again.")

        } catch (e: IOException) {
            Resource.Error("Couldn't reach the server. Check your internet connection.")

        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}

class Authenticate @Inject constructor(
    private val auth: FirebaseAuth
) {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend operator fun invoke(email: String, password: String): Resource<LoginData> {
        return try {
            Resource.Loading<LoginData>()
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                val uid = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")
                Constants.tempData["uid"] = uid
                Resource.Success(LoginData(uid))
            } else {
                Resource.Error("Login failed")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Error("Incorrect email or password. Please try again.")
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("This email address is already in use by another account.")
        } catch (e: FirebaseAuthException) {
            Resource.Error("Authentication error: ${e.message}")
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "A network error occurred. Please try again.")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach the server. Check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}

