package com.itolstoy.boardgames.presentation.account

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.common.Constants
import com.itolstoy.boardgames.presentation.Screens
import com.itolstoy.boardgames.presentation.connectivity_network.ConnectivityNetworkScreen

@Composable
fun Account(navController: NavHostController,
            showSnackbar: (String, SnackbarDuration: SnackbarDuration, () -> Unit) -> Unit,
            viewModel: AccountViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val accountScreenState by remember { viewModel.accountScreenState }
    if (accountScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (accountScreenState.error.isNotEmpty()) {
        LaunchedEffect(accountScreenState) {
            val message = accountScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (accountScreenState.isNetworkError) {
        LaunchedEffect(accountScreenState) {
            showSnackbar("No Internet connection!", SnackbarDuration.Indefinite, {viewModel.loadCurrentGamer()})
        }
    }
    if (viewModel.gamer != null) {
        val gamer = viewModel.gamer!!

        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            imageUri?.let {
                viewModel.updateUserImageInFirebase(gamer.gamerId, imageUri)
            }
        }
        Box(modifier = Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(gamer.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 16.dp)
                            .clip(CircleShape)
                            .width(270.dp)
                            .height(270.dp)
                            .align(Alignment.Center),
                        error = painterResource(id = R.drawable.ic_no_image)
                    )
                    IconButton(modifier = Modifier.align(Alignment.TopEnd),
                        onClick = { galleryLauncher.launch(Constants.ALL_IMAGES) }) {
                        Icon(Icons.Default.Edit, "Edit image")
                        if (accountScreenState.isImageAddedToFirebase) {
                            viewModel.loadCurrentGamer()
                        }
                    }
                }
                Text(text = gamer.name,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(12.dp))
            }
            Text(text = "Logout",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomCenter)
                    .clickable {
                        viewModel.signOut()
                    })
            if (accountScreenState.isSignOut) {
                LaunchedEffect(accountScreenState) {
                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(0)
                    }
                }
            }
        }
    }
}