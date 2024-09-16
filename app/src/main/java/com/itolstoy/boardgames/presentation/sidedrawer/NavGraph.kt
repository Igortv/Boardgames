package com.itolstoy.boardgames.presentation.sidedrawer

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.itolstoy.boardgames.presentation.AUTH_GRAPH_ROUTE
import com.itolstoy.boardgames.presentation.ROOT_GRAPH_ROUTE

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AUTH_GRAPH_ROUTE,
        route = ROOT_GRAPH_ROUTE
        ) {
        //authNavGraph(navController)
        //homeNavGraph(navController)
    }
}