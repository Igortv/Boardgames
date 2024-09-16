package com.itolstoy.boardgames.presentation.game

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.presentation.Screens
import com.itolstoy.boardgames.presentation.connectivity_network.ConnectivityNetworkScreen

@Composable
fun GameDetailScreen(
    navController: NavHostController,
    viewModel: GameDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val gameDetailScreenState by remember { viewModel.gameDetailScreenState }
    if (gameDetailScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (gameDetailScreenState.error.isNotEmpty()) {
        LaunchedEffect(gameDetailScreenState) {
            val message = gameDetailScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (gameDetailScreenState.isNetworkError) {
        ConnectivityNetworkScreen{}
    }

    if (gameDetailScreenState.game != null && gameDetailScreenState.sessions != null) {
        val game = gameDetailScreenState.game!!
        val sessions = gameDetailScreenState.sessions!!

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.abc_vector_test),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(16.dp)
                    )

                    Column() {
                        Text(
                            text = game.name,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp
                        )
                        Text(
                            text = game.description,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp
                        )
                    }
                }
                Text(
                    text = "Leaderboard",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )

                val gamers = listOf("Gamer1", "Gamer2", "Gamer3", "Gamer4")
                LazyColumn {
                    items(items = gamers) { gamer ->
                        Box {
                            Text(text = gamer)
                        }
                    }
                }
                Text(
                    text = "Sessions",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )

                LazyColumn {
                    items(items = sessions) { session ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .clickable {
                                navController.navigate(Screens.SessionDetailScreen.route + "/${session.sessionId}")
                            }) {
                            Text(text = session.date)
                        }
                    }
                }
            }
        }
    }
}