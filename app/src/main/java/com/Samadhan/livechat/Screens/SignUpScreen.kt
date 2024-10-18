package com.Samadhan.livechat.Screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.Samadhan.livechat.CircularProgressBar
import com.Samadhan.livechat.R
import com.Samadhan.livechat.data.UIEvent
import com.Samadhan.livechat.navGraph.Screen
import com.Samadhan.livechat.viewModel.LCViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun SignUpScreen(navController: NavController,viewModel: LCViewModel = hiltViewModel()) {
    val nameState = remember {
        mutableStateOf("")
    }
    val numberState = remember {
        mutableStateOf("")
    }
    val emailState = remember {
        mutableStateOf("")
    }
    val passwordState = remember {
        mutableStateOf("")
    }
    val isLoading = remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.signUpState.collectLatest { event ->
            when (event) {
                is UIEvent.Success -> {
                    viewModel.createOrUpdateProfile(nameState.value,numberState.value)
                    isLoading.value = false
                }

                is UIEvent.ShowSnackbar -> {
                    isLoading.value = false
                }
                is UIEvent.UserAlreadyExist -> {
                    isLoading.value = false
                }
                is UIEvent.Loading -> {
                    isLoading.value = true

                }
                else -> Unit
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.createOrUpdateUser.collectLatest { event ->
            when (event) {
                is UIEvent.Success -> {
                    isLoading.value = false
                    navController.navigate(Screen.Login.route)
                }

                is UIEvent.ShowSnackbar -> {
                    isLoading.value = false
                }

                is UIEvent.Loading -> {
                    isLoading.value = true
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Light background color
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .padding(top = 32.dp), // Increased top padding for spacing
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sing_up),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 10.dp),
                tint = Color.Unspecified
            )

            Text(
                text = "Create Account",
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF343A40),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text(text = "Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color(0xFF007BFF),
                    unfocusedLabelColor = Color(0xFF6C757D),
                ),
                textStyle = TextStyle(
                    color = Color(0xFF343A40)
                )
            )

            OutlinedTextField(
                value = numberState.value,
                onValueChange = { numberState.value = it },
                label = { Text(text = "Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Move to the next field
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() } // Hide keyboard on "Next"
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color(0xFF007BFF),
                    unfocusedLabelColor = Color(0xFF6C757D),
                ),
                textStyle = TextStyle(
                    color = Color(0xFF343A40)
                )
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text(text = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Move to the next field
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() } // Hide keyboard on "Next"
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color(0xFF007BFF),
                    unfocusedLabelColor = Color(0xFF6C757D),
                ),
                textStyle = TextStyle(
                    color = Color(0xFF343A40)
                )
            )

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text(text = "Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp) // More bottom padding for spacing
                    .background(Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Move to the next field
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() } // Hide keyboard on "Next"
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color(0xFF007BFF),
                    unfocusedLabelColor = Color(0xFF6C757D),
                ),
                textStyle = TextStyle(
                    color = Color(0xFF343A40)
                )
            )

            Button(
                onClick = {
                    viewModel.signUp(emailState.value, passwordState.value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(Color(0xFF007BFF)), // Button color
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007BFF),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Sign Up")
            }

            // Navigation Text
            Text(
                text = "Already a User? Go to Login",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { navController.navigate(Screen.Login.route) },
                color = Color(0xFF007BFF), // Color for clickable text
                fontWeight = FontWeight.Bold
            )
        }

        CircularProgressBar(isLoading.value)
    }
}