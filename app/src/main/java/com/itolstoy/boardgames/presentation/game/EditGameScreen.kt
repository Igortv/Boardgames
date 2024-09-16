package com.itolstoy.boardgames.presentation.game

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.presentation.utils.setResult

@Composable
fun EditGameScreen(navController: NavController, viewModel: EditGameViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val editGameScreenState by remember { viewModel.editGameScreenState }
    val updateConfirmDialog = remember { mutableStateOf(false) }
    val deleteConfirmDialog = remember { mutableStateOf(false) }

    var game: Game? by remember { mutableStateOf(null) }
    var imagePainterUrl by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        imageUri?.let {
            imagePainterUrl = imageUri.toString()
        }
    }

    var gameName by remember {
        mutableStateOf("")
    }
    var gameDescription by remember {
        mutableStateOf("")
    }
    var gameComplexity by remember {
        mutableStateOf("")
    }
    if (editGameScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (editGameScreenState.error.isNotEmpty()) {
        LaunchedEffect(editGameScreenState) {
            val message = editGameScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    editGameScreenState.game?.let { _game ->
        LaunchedEffect(editGameScreenState.game) {
            game = _game
            imagePainterUrl = _game.imageUrl
            gameName = _game.name
            gameDescription = _game.description
            gameComplexity = _game.complexity.toString()
        }
    }

    if (game != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            if (updateConfirmDialog.value) {
                ShowConfirmDialog(
                    title = "Save",
                    message = "Do you want to save this?",
                    confirmAction = {
                        updateConfirmDialog.value = false
                        viewModel.updateGame(
                            game!!.gameId,
                            gameName,
                            gameDescription,
                            imagePainterUrl,
                            gameComplexity
                        )
                    },
                    dismissAction = { updateConfirmDialog.value = false },
                    dismissRequest = { updateConfirmDialog.value = false })
            } else if (deleteConfirmDialog.value) {
                ShowConfirmDialog(
                    title = "Delete",
                    message = "Are you sure you want to delete this?",
                    confirmAction = {
                        deleteConfirmDialog.value = false
                        viewModel.deleteGame(game!!)
                    },
                    dismissAction = { deleteConfirmDialog.value = false },
                    dismissRequest = { deleteConfirmDialog.value = false })
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imagePainterUrl)
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
                OutlinedTextField(value = gameName, onValueChange = {
                    gameName = it
                },
                    modifier = Modifier.padding(10.dp),
                    label = {
                        Text(text = "Game name")
                    })
                OutlinedTextField(value = gameDescription, onValueChange = {
                    gameDescription = it
                },
                    modifier = Modifier.padding(10.dp),
                    label = {
                        Text(text = "Game description")
                    })
                OutlinedTextField(
                    value = gameComplexity, onValueChange = {
                        gameComplexity = it
                    },
                    modifier = Modifier.padding(10.dp),
                    label = {
                        Text(text = "Game complexity")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row {
                    Button(
                        onClick = {
                            updateConfirmDialog.value = true
                        }
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 18.sp
                        )
                        if (editGameScreenState.gameUpdated) {
                            LaunchedEffect(editGameScreenState) {
                                Toast
                                    .makeText(context, "Game updated", Toast.LENGTH_SHORT)
                                    .show()
                                navController.setResult(keyResult = "action", value = "update")
                                navController.popBackStack()
                            }
                        }
                    }
//                    Button(
//                        onClick = {
//                            deleteConfirmDialog.value = true
//                        }
//                    ) {
//                        Text(
//                            text = "Delete",
//                            fontSize = 18.sp
//                        )
//                        if (editGameScreenState.gameDeleted) {
//                            LaunchedEffect(editGameScreenState) {
//                                Toast
//                                    .makeText(context, "Game deleted", Toast.LENGTH_SHORT)
//                                    .show()
//                                navController.setResult(keyResult = "action", value = "update")
//                                navController.popBackStack()
//                            }
//                        }
//                    }
                }
            }
        }
    }
}

@Composable
fun ShowConfirmDialog(title: String, message: String, confirmAction: () -> Unit, dismissAction: () -> Unit, dismissRequest: () -> Unit) {
    AlertDialog(onDismissRequest =  { dismissRequest.invoke() },
    title = {
        Text(text = title)
            },
    text = {
        Text(text = message)
    },
    confirmButton = {
        TextButton(onClick = { confirmAction.invoke() })
        { Text(text = "OK") }
    },
    dismissButton = {
        TextButton(onClick = { dismissAction.invoke() })
        { Text(text = "Cancel") }
    })
}
