package com.itolstoy.boardgames.presentation.game

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.common.Constants
import com.itolstoy.boardgames.presentation.utils.setResult

@Composable
fun AddGameScreen(navController: NavController, viewModel: AddGameViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val addGameScreenState by remember { viewModel.addGameScreenState }
    val imagePainterUrl = remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        imageUri?.let {
            imagePainterUrl.value = imageUri.toString()
        }
    }

    val gameNameState = remember {
        mutableStateOf("")
    }
    val gameDescriptionState = remember {
        mutableStateOf("")
    }
    val gameComplexityState = remember {
        mutableStateOf("1.0")
    }

    if (addGameScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (addGameScreenState.error.isNotEmpty()) {
        LaunchedEffect(addGameScreenState) {
            val message = addGameScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(model = ImageRequest.Builder(context)
                .data(imagePainterUrl.value)
                .crossfade(true)
                .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .clip(CircleShape)
                    .width(270.dp)
                    .height(270.dp),
                error = painterResource(id = R.drawable.ic_no_image)
            )
            Button(
                onClick = {
                    galleryLauncher.launch(Constants.ALL_IMAGES)
                }
            ) {
                Text(
                    text = Constants.OPEN_GALLERY,
                    fontSize = 18.sp
                )
            }
            OutlinedTextField(value = gameNameState.value, onValueChange = {
                gameNameState.value = it
            },
                modifier = Modifier.padding(10.dp),
                label = {
                    Text(text = "Enter game name")
                })
            OutlinedTextField(value = gameDescriptionState.value, onValueChange = {
                gameDescriptionState.value = it
            },
                modifier = Modifier.padding(10.dp),
                label = {
                    Text(text = "Enter game description")
                })
            OutlinedTextField(value = gameComplexityState.value, onValueChange = {
                gameComplexityState.value = it
            },
                modifier = Modifier.padding(10.dp),
                label = {
                    Text(text = "Enter game complexity")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = {
                    viewModel.createGame(gameNameState.value, gameDescriptionState.value, imagePainterUrl.value, gameComplexityState.value)
                }
            ) {
                Text(
                    text = "Create",
                    fontSize = 18.sp
                )
                if (addGameScreenState.gameCreated) {
                    LaunchedEffect(addGameScreenState) {
                        Toast
                            .makeText(context, "Game created", Toast.LENGTH_SHORT)
                            .show()
                        navController.setResult(keyResult = "action", value = "update")
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
