package com.itolstoy.boardgames.presentation.sessions

import android.content.Context
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.presentation.Screens
import com.itolstoy.boardgames.presentation.arch.navigate
import com.itolstoy.boardgames.presentation.utils.getOnceResult

@Composable
fun SessionsScreen(
    navController: NavHostController,
    showSnackbar: (String, SnackbarDuration: SnackbarDuration, () -> Unit) -> Unit,
    viewModel: SessionsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isRefreshing = viewModel.isRefreshing.collectAsState().value
    val isAdmin = viewModel.getAdminValue()
    val sessionsScreenState by remember { viewModel.sessionsScreenState }
    val sessions = sessionsScreenState.sessions
    navController.getOnceResult<String>(keyResult = "action") {
        if (it == "update") {
            viewModel.getSessions()
        }
    }
    if (sessionsScreenState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    if (sessionsScreenState.error.isNotEmpty()) {
        val message = sessionsScreenState.error
        Toast
            .makeText(context, message, Toast.LENGTH_SHORT)
            .show()
    }
    if (sessionsScreenState.isNetworkError) {
        LaunchedEffect(sessionsScreenState) {
            showSnackbar("No Internet connection!", SnackbarDuration.Indefinite, {viewModel.getSessions()})
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
            Column(modifier = Modifier.padding(8.dp)) {
                if (isAdmin) {
                    Button(onClick = {
                        navController.navigate(Screens.AddSessionScreen.route)
                    }) {
                        Text(
                            text = "Add Session",
                            fontSize = 18.sp
                        )
                    }
                }
                SessionListScreen(navController, context, sessions = sessions, isAdmin)
            }
        }
    }
}

@Composable
fun SessionListScreen(navController: NavHostController, context: Context, sessions: List<Session>, isAdmin: Boolean) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(items = sessions) { session->
            Row(modifier = Modifier.padding(8.dp)) {
                Box (
                    modifier = Modifier.clickable {
                        if (isAdmin) {
                            navController.navigate(Screens.EditSessionScreen.route + "/${session.sessionId}")
                        } else {
//                            navController.navigate(
//                                Screens.SessionDetailScreen.route,
//                                bundleOf("session" to session)
//                            )
                            navController.navigate(Screens.SessionDetailScreen.route + "/${session.sessionId}")
                        }
                    }
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(text = session.gameName)
                        Text(text = session.date)
                        AsyncImage(model = ImageRequest.Builder(context)
                            .data(session.winnerImageUrl)
                            .crossfade(true)
                            .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 16.dp)
                                .clip(CircleShape)
                                .width(50.dp)
                                .height(50.dp),
                            error = painterResource(id = R.drawable.ic_no_image)
                        )
                    }
                }
            }
        }
    }
}