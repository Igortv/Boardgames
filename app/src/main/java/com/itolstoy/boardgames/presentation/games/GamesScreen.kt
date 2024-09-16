package com.itolstoy.boardgames.presentation.games

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.presentation.Screens
import com.itolstoy.boardgames.presentation.utils.getOnceResult

@Composable
fun GamesScreen(
    navController: NavHostController,
    showSnackbar: (String, SnackbarDuration: SnackbarDuration, () -> Unit) -> Unit,
    viewModel: GamesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isAdmin = viewModel.getAdminValue()
    val isRefreshing = viewModel.isRefreshing.collectAsState().value
    val gamesScreenState by remember { viewModel.gamesScreenState }

    navController.getOnceResult<String>(keyResult = "action") {
        if (it == "update") {
            viewModel.getGames()
        }
    }
    if (gamesScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (gamesScreenState.error.isNotEmpty()) {
        LaunchedEffect(gamesScreenState) {
            val message = gamesScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (gamesScreenState.isNetworkError) {
        LaunchedEffect(gamesScreenState) {
            showSnackbar("No Internet connection!", SnackbarDuration.Indefinite, {viewModel.getGames()})
        }
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
            onRefresh = { viewModel.refresh() },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    contentColor = MaterialTheme.colors.primary
                )
            }
        ) {
            if (gamesScreenState.games != null) {
                val games = gamesScreenState.games!!

                Column(modifier = Modifier.padding(8.dp)) {
                    if (isAdmin) {
                        Button(onClick = {
                            navController.navigate(Screens.AddGameScreen.route) {
                                launchSingleTop = true
                            }
                        }) {
                            Text(
                                text = "Add Game",
                                fontSize = 18.sp
                            )
                        }
                    }
                    LazyColumn {
                        items(items = games.chunked(3)) { row ->
                            Row(Modifier.fillParentMaxWidth()) {
                                for ((index, item) in row.withIndex()) {
                                    Box(Modifier.fillMaxWidth(1f / (3 - index))) {
                                        GameItem(navController, item, isAdmin)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameItem(navController: NavHostController, game: Game, isAdmin: Boolean) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .clickable {
                if (isAdmin) {
                    navController.navigate(Screens.EditGameScreen.route + "/${game.gameId}")
                } else {
                    navController.navigate(Screens.GameScreen.route + "/${game.gameId}")
                }
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp)
        )
        {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(model = ImageRequest.Builder(context)
                    .data(game.imageUrl)
                    .crossfade(true)
                    .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 16.dp)
                        .size(100.dp)
                        .clip(CircleShape),
                    error = painterResource(id = R.drawable.ic_no_image)
                )
                Text(
                    text = game.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GamesScreenPreview() {
//    GamesScreen(navController = rememberNavController())
//}