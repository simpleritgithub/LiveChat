package com.Samadhan.livechat.viewModel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Samadhan.livechat.data.LoginData
import com.Samadhan.livechat.data.UIEvent
import com.Samadhan.livechat.di.Resource
import com.Samadhan.livechat.useCase.AuthUseCase
import com.Samadhan.livechat.useCase.Authenticate
import com.Samadhan.livechat.useCase.CreateOrUpdateUseCase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val createOrUpdateUseCase: CreateOrUpdateUseCase,
    private val authenticate: Authenticate,
    private val firebaseStorage: FirebaseStorage,
) : ViewModel() {

    private val _signUpState = MutableStateFlow<UIEvent>(UIEvent.Empty)
    val signUpState: StateFlow<UIEvent>  = _signUpState

    private val _createOrUpdateUser = MutableStateFlow<UIEvent>(UIEvent.Empty)
    val createOrUpdateUser: StateFlow<UIEvent>  = _createOrUpdateUser

    private val _loginState = MutableStateFlow<UIEvent>(UIEvent.Empty)
    val loginState: StateFlow<UIEvent>  = _loginState

    private val _login = MutableStateFlow<LoginData?>(null)
    val login: MutableStateFlow<LoginData?> = _login

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _signUpState.emit(UIEvent.Loading)
            val result = authUseCase(email, password)
            when (result) {
                is Resource.Success -> {
                    _signUpState.emit(UIEvent.Success)
                }
                is Resource.Error -> {
                    if (result.message == "The email address is already in use by another account"){
                        _signUpState.emit(UIEvent.UserAlreadyExist)
                    }else{
                        _signUpState.emit(UIEvent.ShowSnackbar(result.message?:""))
                    }
                }
                is Resource.Loading -> {
                    _signUpState.emit(UIEvent.Loading)
                }
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun createOrUpdateProfile(name: String? = null, number: String? = null, imageUrl: String? = null) {
        viewModelScope.launch {
            _createOrUpdateUser.emit(UIEvent.Loading)
            val result = createOrUpdateUseCase(name, number, imageUrl) // Correct invocation
            when (result) {
                is Resource.Success -> {
                    _createOrUpdateUser.emit(UIEvent.Success)
                }
                is Resource.Error -> {
                    _createOrUpdateUser.emit(UIEvent.ShowSnackbar(result.message ?: "An error occurred"))
                }
                is Resource.Loading -> {
                    _createOrUpdateUser.emit(UIEvent.Loading)
                }
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun login(email: String, password: String){
        viewModelScope.launch {
            _loginState.emit(UIEvent.Loading)
            val result = authenticate(email,password)
            when (result) {
                is Resource.Success -> {
                    val uid = result.data?.userId
                    _login.value = LoginData(uid)
                    _loginState.emit(UIEvent.Success)
                }
                is Resource.Error -> {
                    _loginState.emit(UIEvent.ShowSnackbar(result.message ?: "An error occurred"))
                }
                is Resource.Loading -> {
                    _loginState.emit(UIEvent.Loading)
                }
            }
        }

    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun uploadProfileImage(uri: Uri){
        uploadImage(uri){
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri,onsuccess:(Uri) ->Unit){
        val storageRef = firebaseStorage.reference
        val uUi = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uUi")
        imageRef.putFile(uri).addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onsuccess)
        }
    }
}
