package com.Samadhan.livechat.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import com.Samadhan.livechat.data.Constants
import com.Samadhan.livechat.data.DataStore
import com.Samadhan.livechat.data.UIEvent
import com.Samadhan.livechat.navGraph.Screen
import com.Samadhan.livechat.viewModel.LCViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController,viewModel: LCViewModel= hiltViewModel()) {
    val emailState = remember {
        mutableStateOf("")
    }
    val passwordState = remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val loginData = viewModel.login.collectAsState().value
    val isLoading = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = true) {
        dataStore.setIpsaDataStoreVal("app_initial", "login")
        viewModel.loginState.collectLatest { event ->
            when (event) {
                is UIEvent.Success -> {
                    isLoading.value = false
                    dataStore.setIpsaDataStoreVal("uid", Constants.tempData["uid"]?:"")
                    navController.navigate(Screen.ChatList.route)
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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
            .imePadding()
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
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.login),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .padding(bottom = 16.dp) ,
                tint = Color.Unspecified
            )

            Text(
                text = "SIGN IN",
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF343A40),
                modifier = Modifier.padding(bottom = 16.dp)
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
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
                    .padding(bottom = 16.dp)
                    .background(Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color(0xFF007BFF),
                    unfocusedLabelColor = Color(0xFF6C757D),
                ),
                textStyle = TextStyle(
                    color = Color(0xFF343A40)
                )
            )

            // Login Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    viewModel.login(
                        emailState.value,
                        passwordState.value
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), // Increased padding for button
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)) // Button color
            ) {
                Text(text = "Login", color = Color.White) // White text for contrast
            }

            // Navigation to Sign Up
            Text(
                text = "Don't have an account? Create one ->",
                color = Color(0xFF007BFF), // Link color
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate(Screen.SignUp.route)
                    }
            )
        }
        CircularProgressBar(isLoading.value)

    }
}