package com.itolstoy.boardgames.presentation.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.presentation.Screens

@Composable
fun SignUpScreen(navController: NavHostController, viewModel: SignUpViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val signUpScreenState by remember { viewModel.signUpScreenState }
    val userNameState = remember {
        mutableStateOf("")
    }
    val emailState = remember {
        mutableStateOf("")
    }
    val passwordState = remember {
        mutableStateOf("")
    }
    if (signUpScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (signUpScreenState.error.isNotEmpty()) {
        LaunchedEffect(signUpScreenState) {
            val message = signUpScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    Box(modifier = Modifier.fillMaxSize()
        .padding(8.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.abc_vector_test),
                contentDescription = "LoginScreen Logo",
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Sign Up",
                modifier = Modifier.padding(10.dp),
                fontSize = 30.sp)
            OutlinedTextField(value = userNameState.value, onValueChange = {
                userNameState.value = it
            },
                modifier = Modifier.padding(10.dp),
                label = {
                    Text(text = "Enter your user name:")
                })
            OutlinedTextField(value = emailState.value, onValueChange = {
                emailState.value = it
            },
                modifier = Modifier.padding(10.dp),
                label = {
                    Text(text = "Enter your email:")
                })
            OutlinedTextField(value = passwordState.value, onValueChange = {
                passwordState.value = it
            },
                modifier = Modifier.padding(10.dp),
                label = {
                    Text(text = "Enter your password:")
                },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = {
                viewModel.signUp(userNameState.value, emailState.value, passwordState.value)
            }, modifier = Modifier.padding(8.dp)) {
                Text(text = "Sign Up")
                if (signUpScreenState.isSignUp) {
                    LaunchedEffect(signUpScreenState) {
                        Toast
                            .makeText(context, "Check your email", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            Text(text = "Already a User? Sign In", color = Color.Blue, modifier = Modifier
                .padding(8.dp)
                .clickable {
                    navController.navigate(Screens.LoginScreen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}