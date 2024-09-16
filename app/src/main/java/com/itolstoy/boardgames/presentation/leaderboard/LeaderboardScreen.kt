package com.itolstoy.boardgames.presentation.leaderboard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.presentation.Screens

@Composable
fun LeaderboardScreen(
    navController: NavHostController,
    showSnackbar: (String, SnackbarDuration: SnackbarDuration, () -> Unit) -> Unit,
    viewModel: LeaderBoardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isRefreshing = viewModel.isRefreshing.collectAsState().value
    val leaderboardScreenState by remember { viewModel.leaderboardScreenState }
    val gamers = leaderboardScreenState.gamers

    if (leaderboardScreenState.isLoading) {
        Log.d("leaderboardScreenState", "leaderboardScreenState.isLoading")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (leaderboardScreenState.error.isNotEmpty()) {
        LaunchedEffect(leaderboardScreenState) {
            val message = leaderboardScreenState.error
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (leaderboardScreenState.isNetworkError) {
        Log.d("leaderboardScreenState", "leaderboardScreenState.isNetworkError")
        LaunchedEffect(leaderboardScreenState.isNetworkError) {
            showSnackbar("No Internet connection!", SnackbarDuration.Indefinite,
                { viewModel.getLeaderBoardList() })
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
            LeaderBoardListScreen(navController, gamers = gamers)
        }
    }
}

@Composable
fun LeaderBoardListScreen(navController: NavHostController, gamers: List<Gamer>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(items = gamers) { gamer->
            Row(modifier = Modifier.padding(8.dp)) {
                LeaderBoardRow(navController, gamer)
            }
        }
    }
}

@Composable
fun LeaderBoardRow(navController: NavHostController, gamer: Gamer) {
    //Column(modifier = Modifier.padding(all = 10.dp)
        //.clickable { navController.navigate(Screens.ProfileScreen.route, bundleOf("gamer" to gamer)) }){
        Text(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screens.ProfileScreen.route + "/" + gamer.gamerId)
                //navController.navigate(Screens.ProfileScreen.route, bundleOf("gamer" to gamer))
            }
            .padding(all = 10.dp),
            text = gamer.name)
    //}
}

//@Preview(showBackground = true)
//@Composable
//fun LeaderboardScreenPreview() {
//    LeaderboardScreen(rememberNavController())
//}
