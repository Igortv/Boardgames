package com.itolstoy.boardgames.presentation.sidedrawer

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.itolstoy.boardgames.presentation.HOME_GRAPH_ROUTE
import com.itolstoy.boardgames.presentation.Screens

//fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
//    navigation(
//        startDestination = "main_screen",
//        route = HOME_GRAPH_ROUTE
//    ) {
//        composable(
//            route = "main_screen") {
//            MainScreen(navController = navController)
//        }
//    }
//}

//@Composable
//fun homeNavGraph(navController: NavHostController) {
//    NavHost(
//        navController = navController,
//        startDestination = Screens.LeaderboardScreen.route,//"main_screen",
//        route = HOME_GRAPH_ROUTE
//    ) {
//        composable(
//            route = "main_screen") {
//            MainScreen(navController = navController)
//        }
//    }
//}