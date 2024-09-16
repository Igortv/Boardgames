package com.itolstoy.boardgames.presentation.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.presentation.Screens
import com.itolstoy.boardgames.presentation.connectivity_network.ConnectivityNetworkScreen

@Composable
fun SplashScreen(navController: NavController, viewModel: SplashViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val splashScreenState by remember { viewModel.splashScreenState }
    if (splashScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (splashScreenState.error.isNotEmpty()) {
        LaunchedEffect(splashScreenState) {
            val message = splashScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (splashScreenState.noConnection) {
        ConnectivityNetworkScreen {
            viewModel.checkInternet()
        }
    }
    if (splashScreenState.authenticationStatus != AuthenticationStatus.UNDEFINED) {
        LaunchedEffect(splashScreenState) {
            if (splashScreenState.authenticationStatus == AuthenticationStatus.AUTHENTICATED) {
                navController.navigate(route = Screens.LeaderboardScreen.route) {
                    popUpTo(Screens.SplashScreen.route) {
                        inclusive = true
                    }
                }
            } else {
                navController.navigate(Screens.LoginScreen.route) {
                    popUpTo(Screens.SplashScreen.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
    }
}