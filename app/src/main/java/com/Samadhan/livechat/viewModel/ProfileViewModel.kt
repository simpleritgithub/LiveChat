package com.Samadhan.livechat.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Samadhan.livechat.data.UIEvent
import com.Samadhan.livechat.data.UserProfile
import com.Samadhan.livechat.di.Resource
import com.Samadhan.livechat.useCase.ProfileUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _profileState = MutableStateFlow<UIEvent>(UIEvent.Empty)
    val profileState: StateFlow<UIEvent> = _profileState

    private val _profileData = MutableStateFlow<UserProfile?>(null)
    val profileData: StateFlow<UserProfile?> = _profileData

    fun getProfileData(userId: String) {
        viewModelScope.launch {
            _profileState.emit(UIEvent.Loading)
            val result = profileUseCase(userId)
            when (result) {
                is Resource.Success -> {
                    val profile = result.data
                    profile?.let {
                        _profileData.emit(it)
                    } ?: run {
                    }
                    _profileState.emit(UIEvent.Success)
                }

                is Resource.Error -> {
                    _profileState.emit(UIEvent.ShowSnackbar(result.message ?: ""))
                }

                is Resource.Loading -> {
                    _profileState.emit(UIEvent.Loading)
                }
            }
        }
    }

    fun logOut(){
        auth.signOut()
    }
}