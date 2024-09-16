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
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.presentation.game.ShowConfirmDialog
import com.itolstoy.boardgames.presentation.utils.setResult
import java.util.*

@Composable
fun EditSessionScreen(navController: NavController, viewModel: EditSessionViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val editSessionScreenState by remember { viewModel.editSessionScreenState }
    val updateConfirmDialog = remember { mutableStateOf(false) }
    val deleteConfirmDialog = remember { mutableStateOf(false) }

    var session: Session? by remember { mutableStateOf(null) }

    var games by remember {
        mutableStateOf<List<Game>?>(null)
    }
    var selectedGame: Game? by remember { mutableStateOf(null)}

    var gamers: List<Gamer>? by remember {
        mutableStateOf(null)
    }
    var selectedGamers = remember {
        mutableListOf<Gamer>().toMutableStateList()
    }
    var initialOrderedParticipants = remember {
        mutableListOf<Gamer>().toMutableStateList()
    }
    var gameListExpanded by remember { mutableStateOf(false) }

    val year: Int
    val month: Int
    val day: Int

    var date by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, _year: Int, _month: Int, _dayOfMonth: Int ->
            date = "$_dayOfMonth/${_month+1}/$_year"
        }, year, month, day
    )

    val openGamersDialog = remember { mutableStateOf(false)  }
    var gameName by remember { mutableStateOf("") }

    if (editSessionScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (editSessionScreenState.error.isNotEmpty()) {
        LaunchedEffect(editSessionScreenState) {
            val message = editSessionScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    editSessionScreenState.session?.let { _session ->
        LaunchedEffect(editSessionScreenState.session) {
            session = _session
            date = _session.date
            gameName = _session.gameName
            games = editSessionScreenState.games
            gamers = editSessionScreenState.gamers
            selectedGame = games!![0]
        }
    }
    if (session != null && games != null && gamers != null) {
        val gamers = gamers!!
        val games = games!!
        val session = session!!

        selectedGame = games.first { it.name == session.gameName }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
            ) {
                item {
                    if (updateConfirmDialog.value) {
                        ShowConfirmDialog(
                            title = "Save",
                            message = "Do you want to save this?",
                            confirmAction = {
                                updateConfirmDialog.value = false
                                viewModel.updateSession(
                                    session.sessionId,
                                    selectedGamers,
                                    date = date,
                                    selectedGame
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
                                viewModel.deleteSession(session)
                            },
                            dismissAction = { deleteConfirmDialog.value = false },
                            dismissRequest = { deleteConfirmDialog.value = false })
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { gameListExpanded = !gameListExpanded }) {
                        Text(text = gameName, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
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
                        openGamersDialog.value = true
                    }) {
                        Text("Add players")
                    }
                }
                if (selectedGamers.isEmpty() && (selectedGamers equalsIgnoreOrder initialOrderedParticipants)) {
                    for (i in 0..session.participants.size - 1) {
                        initialOrderedParticipants.add(gamers.first { it.gamerId == session.participants[i] })
                    }
                    selectedGamers.addAll(initialOrderedParticipants)
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
                    if (openGamersDialog.value) {

                        val notAddedGamers = gamers.map { it }.filterNot { selectedGamers.contains(it) }

                        AlertDialog(onDismissRequest = {
                            openGamersDialog.value = false
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
                                                    openGamersDialog.value = false
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
                                        openGamersDialog.value = false
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
                        text = "Selected Date: ${date}",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = {
                            updateConfirmDialog.value = true
//                        viewModel.updateSession(
//                            session.sessionId,
//                            selectedGamers,
//                            date = date.value,
//                            selectedGame
//                        )
                        }) {
                            Text(text = "Update session", fontSize = 18.sp)
                            if (editSessionScreenState.sessionUpdated) {
                                LaunchedEffect(editSessionScreenState) {
                                    Toast
                                        .makeText(context, "Session updated", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.setResult(keyResult = "action", value = "update")
                                    navController.popBackStack()
                                }
                            }
                        }
//                        Button(onClick = {
//                            deleteConfirmDialog.value = true
//                        }) {
//                            Text(text = "Delete session", fontSize = 18.sp)
//                            if (editSessionScreenState.sessionDeleted) {
//                                LaunchedEffect(editSessionScreenState) {
//                                    Toast
//                                        .makeText(context, "Session deleted", Toast.LENGTH_SHORT)
//                                        .show()
//                                    navController.setResult(keyResult = "action", value = "update")
//                                    navController.popBackStack()
//                                }
//                            }
//                        }
                    }
                }
            }
        }
    }
}

infix fun <T> List<T>.equalsIgnoreOrder(other: List<T>) = this.size == other.size && this.toSet() == other.toSet()