package com.itolstoy.boardgames.presentation.session

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.model.Gamer

@Composable
fun SessionDetailScreen(navController: NavHostController, viewModel: SessionDetailViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val sessionScreenState by remember { viewModel.sessionDetailScreenState }

    if (sessionScreenState.isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator()
        }
    }
    if (sessionScreenState.error.isNotEmpty()) {
        val message = sessionScreenState.error
        Toast
            .makeText(context, message, Toast.LENGTH_SHORT)
            .show()
    }
    if (sessionScreenState.gamers != null && sessionScreenState.session != null) {
        val gamePlayers = sessionScreenState.gamers
        val session = sessionScreenState.session
        Box {
            Column() {
                Text(text = "Session date: ${session!!.date}")
                LazyColumn {
                    itemsIndexed(items = gamePlayers) { index, gamer ->
                        SessionPageParticipant(context, index + 1, gamer)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionPageParticipant(context: Context, place: Int, gamer: Gamer) {
    Box(modifier = Modifier.padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$place)")
            AsyncImage(model = ImageRequest.Builder(context)
                .data(gamer.imageUrl)
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
            Text(text = gamer.name)
            Text(text = gamer.averageScore.toString())
        }
    }
}