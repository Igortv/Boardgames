package com.itolstoy.boardgames.presentation.authentication

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val loginScreenState by remember { viewModel.loginScreenState }

    val emailState = rememberSaveable {
        mutableStateOf("")
    }
    val passwordState = rememberSaveable {
        mutableStateOf("")
    }
    if (loginScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (loginScreenState.error.isNotEmpty()) {
        LaunchedEffect(loginScreenState) {
            val message = loginScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
        Box (modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = painterResource(id = R.drawable.abc_vector_test),
                    contentDescription = "LoginScreen Logo",
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 16.dp)
                        .padding(8.dp)
                )
                Text(
                    text = "Sign In",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 30.sp)
                Column {
                    OutlinedTextField(value = emailState.value, onValueChange = {
                        emailState.value = it
                    },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        label = {
                            Text(text = "Enter your email:")
                        })
                    OutlinedTextField(
                        value = passwordState.value, onValueChange = {
                            passwordState.value = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        label = {
                            Text(text = "Enter your password:")
                        },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Button(onClick = {
                        viewModel.signIn(emailState.value, passwordState.value)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        Text(text = "Sign In")
                        if (loginScreenState.isSignInSuccessful) {
                            LaunchedEffect(loginScreenState) {
                                navController.navigate(Screens.LeaderboardScreen.route) {
                                    popUpTo(Screens.LoginScreen.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                    Text(text = "Forgot password?", color = Color.Blue, modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            navController.navigate(Screens.ResetPasswordScreen.route)
                        })
                }
            }
            Text(text = "New user? Sign Up", color = Color.Blue, modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(9.dp)
                .clickable {
                    navController.navigate(Screens.SignUpScreen.route)
                })
        }
}