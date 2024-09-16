package com.itolstoy.boardgames.presentation.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.presentation.Screens
import com.itolstoy.boardgames.presentation.arch.navigate

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val profileScreenState by remember { viewModel.profileScreenState }

    if (profileScreenState.isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
    if (profileScreenState.error.isNotEmpty()) {
        val message = profileScreenState.error
        Toast
            .makeText(context, message, Toast.LENGTH_SHORT)
            .show()
    }
    if (profileScreenState.gamer != null && profileScreenState.sessions != null) {
        val gamer = profileScreenState.gamer!!
        val sessions = profileScreenState.sessions!!
        Box(modifier = Modifier.padding(8.dp)) {
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.wrapContentSize()) {
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
                                .width(200.dp)
                                .height(200.dp)
                                .align(Alignment.Center),
                            error = painterResource(id = R.drawable.ic_no_image)
                        )
                        Text(
                            text = gamer.averageScore.toString(),
                            modifier = Modifier.align(Alignment.TopEnd),
                            fontSize = 16.sp
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = gamer.name,
                            fontSize = 24.sp
                        )
                    }
                }
                Text(text = "Sessions")
                Spacer(modifier = Modifier.size(16.dp))
                LazyColumn {
                    items(items = sessions) { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Screens.SessionDetailScreen.route + "/${session.sessionId}")
                                    //navController.navigate(Screens.SessionDetailScreen.route, bundleOf("session" to session))
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = session.gameName, fontSize = 16.sp)
                            Text(text = session.date, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }
    }
//    when(val response = viewModel.viewState.value) {
//        ProfileViewState.OK -> {}
//        ProfileViewState.Loading -> {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//        ProfileViewState.LoadGamerInfo -> {
//            viewModel.getGamerSessions(gamer)
//        }
//        is ProfileViewState.Error -> {
//            val message = response.message
//            Toast
//                .makeText(context, message, Toast.LENGTH_SHORT)
//                .show()
//        }
//        is ProfileViewState.Success -> {
//            val sessions = response.sessions
//            Box(modifier = Modifier.padding(8.dp)) {
//                Column() {
//                    Row(modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween) {
//                        Box(modifier = Modifier.wrapContentSize()) {
//                            AsyncImage(model = ImageRequest.Builder(context)
//                                .data(gamer.imageUrl)
//                                .crossfade(true)
//                                .build(),
//                                contentDescription = null,
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .padding(top = 8.dp, bottom = 16.dp)
//                                    .clip(CircleShape)
//                                    .width(200.dp)
//                                    .height(200.dp)
//                                    .align(Alignment.Center),
//                                error = painterResource(id = R.drawable.ic_no_image)
//                            )
//                            Text(text = gamer.averageScore.toString(), modifier = Modifier.align(Alignment.TopEnd),
//                                fontSize = 16.sp)
//                        }
//                        Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
//                            Text(text = gamer.name,
//                                fontSize = 24.sp)
//                        }
//                    }
//                    Text(text = "Sessions")
//                    Spacer(modifier = Modifier.size(16.dp))
//                    LazyColumn {
//                        items(items = sessions) { session ->
//                            Row(modifier = Modifier.fillMaxWidth()
//                                .clickable {
//                                    navController.navigate(Screens.SessionDetailScreen.route, bundleOf("session" to session))
//                                },
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically) {
//                                Text(text = session.gameName, fontSize = 16.sp)
//                                Text(text = session.date, fontSize = 16.sp)
//                            }
//                            Spacer(modifier = Modifier.size(8.dp))
//                        }
//                    }
//                }
//            }
//        }
//    }

}