package com.itolstoy.boardgames.presentation.session

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.presentation.utils.setResult
import java.util.*

@Composable
fun AddSessionScreen(navController: NavController, viewModel: AddSessionViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val addSessionScreenState by remember { viewModel.addSessionScreenState }
    var games by remember {
        mutableStateOf<List<Game>>(emptyList())
    }
    var selectedGame by remember { mutableStateOf(
        if (games.isNotEmpty()) games[0] else null
    ) }
    var selectedGamers = remember {
        mutableListOf<Gamer>().toMutableStateList()
    }
    var gamers by remember {
        mutableStateOf<List<Gamer>>(emptyList())
    }
    var gameListExpanded by remember { mutableStateOf(false) }

    val year: Int
    val month: Int
    val day: Int

    val date = remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, _year: Int, _month: Int, _dayOfMonth: Int ->
            date.value = "$_dayOfMonth/${_month+1}/$_year"
        }, year, month, day
    )

    val openDialog = remember { mutableStateOf(false)  }

    val gameName = selectedGame?.name ?: "Choose game"
    if (addSessionScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (addSessionScreenState.error.isNotEmpty()) {
        LaunchedEffect(addSessionScreenState) {
            val message = addSessionScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    addSessionScreenState.games?.let { _game ->
        LaunchedEffect(addSessionScreenState.games) {
            games = _game
        }
    }
    addSessionScreenState.gamers?.let { _gamer ->
        LaunchedEffect(addSessionScreenState.gamers) {
            gamers = _gamer
        }
    }
    if (games != null && games != null) {
        if (games.isNotEmpty() && gamers.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    item {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { gameListExpanded = !gameListExpanded }) {
                            Text(
                                text = gameName,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                            DropdownMenu(expanded = gameListExpanded, onDismissRequest = {
                                gameListExpanded = false
                            }) {
                                games.forEach { game ->
                                    DropdownMenuItem(onClick = {
                                        gameListExpanded = false
                                        selectedGame = game
                                    }) {
                                        Text(text = game.name)
                                    }
                                }
                            }
                        }

                        Button(modifier = Modifier.padding(top = 8.dp), onClick = {
                            openDialog.value = true
                        }) {
                            Text("Add players")
                        }
                    }
                    items(selectedGamers) { gamer ->
                        Row {
                            Text(text = gamer.name)
                            IconButton(onClick = { selectedGamers.remove(gamer) }) {
                                Icon(Icons.Default.Delete, "Delete gamer")
                            }
                        }
                        Divider()
                    }
                    item {
                        if (openDialog.value) {

                            val notAddedGamers =
                                gamers.map { it }.filterNot { selectedGamers.contains(it) }

                            AlertDialog(onDismissRequest = {
                                openDialog.value = false
                            }, modifier = Modifier.wrapContentHeight(),
                                title = {
                                    Text(text = "Players")
                                },
                                text = {
                                    LazyColumn {
                                        items(notAddedGamers) { gamer ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight()
                                                    .clickable {
                                                        selectedGamers.add(gamer)
                                                        openDialog.value = false
                                                    },
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = gamer.name, modifier = Modifier
                                                        .padding(16.dp)
                                                )
                                            }
                                            Divider()
                                        }
                                    }
                                },
                                confirmButton = {
                                },
                                dismissButton = {
                                    Button(
                                        onClick = {
                                            openDialog.value = false
                                        }) {
                                        Text("Cancel")
                                    }
                                })
                        }

                        Spacer(modifier = Modifier.size(48.dp))
                        Button(onClick = {
                            datePickerDialog.show()
                        }) {
                            Text(text = "Choose date", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.size(32.dp))

                        Text(
                            text = "Selected Date: ${date.value}",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = {
                            viewModel.createSession(selectedGamers, date = date.value, selectedGame)
                        }) {
                            Text(text = "Create session", fontSize = 18.sp)
                            if (addSessionScreenState.sessionCreated) {
                                LaunchedEffect(addSessionScreenState) {
                                    Toast
                                        .makeText(context, "Session created", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.setResult(keyResult = "action", value = "update")
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
