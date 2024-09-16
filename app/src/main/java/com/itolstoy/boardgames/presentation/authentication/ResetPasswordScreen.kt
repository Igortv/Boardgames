package com.itolstoy.boardgames.presentation.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ResetPasswordScreen(navController: NavHostController, viewModel: ResetPasswordViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val resetPasswordScreenState by remember { viewModel.resetPasswordScreenState }

    val emailState = remember {
        mutableStateOf("")
    }
    if (resetPasswordScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (resetPasswordScreenState.error.isNotEmpty()) {
        LaunchedEffect(resetPasswordScreenState) {
            val message = resetPasswordScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = emailState.value, onValueChange = {
            emailState.value = it
        },
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp),
            label = {
                Text(text = "Enter your email:")
            })
        Button(onClick = {
            viewModel.resetPassword(emailState.value)
        }, modifier = Modifier.padding(8.dp)) {
            Text(text = "Reset password")
            if (resetPasswordScreenState.isResetPasswordSuccessful) {
                LaunchedEffect(resetPasswordScreenState) {
                    Toast
                        .makeText(context, "Check your email", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
