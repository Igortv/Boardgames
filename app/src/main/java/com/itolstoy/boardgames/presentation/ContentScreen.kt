package com.itolstoy.boardgames.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.itolstoy.boardgames.R

const val ROOT_GRAPH_ROUTE = "root"
const val HOME_GRAPH_ROUTE = "home"
const val AUTH_GRAPH_ROUTE = "auth"




@Composable
fun GamerProfile(navController: NavHostController) {
    val sessions = listOf("Session1", "Session2", "Session3", "Session4")

    Column (
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .clickable {
                navController.popBackStack()
            }
    ) {
        Row {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.abc_vector_test), contentDescription = "",
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(text = "Rating", modifier = Modifier.align(Alignment.TopEnd))
            }
            Text(text = "Name")
        }
        Text(text = "Sessions")
        LazyColumn {
            items(items = sessions) { session ->
                Text(text = session)
            }
        }
    }
}

//@Composable
//fun SeparatedScreen() {
//    val navController = rememberNavController()
//
//    Scaffold {
//        NavigationTest(navController = navController)
//    }
//
//}

//@Composable
//fun NavGraphBuilder.testGraph(navController: NavHostController) {
//    navigation(startDestination = Screen.Test.route, route = "testScreen") {
//        composable(Screen.Test.route) {
//            TestScreen1(navController)
//        }
//    }
//}

//sealed class Screen(val route: String) {
//    object SplashScreen : Screen("splash_screen")
//    object LoginScreen : Screen("login_screen")
//    object SignUpScreen : Screen("signup_screen")
//    object Leaderboard : Screen("leaderboard")
//    object Games : Screen("games")
//    object Game : Screen("game")
//    object AddGame : Screen("add_game")
//    object EditGame : Screen("edit_game")
//    object Sessions : Screen("sessions")
//    object Session : Screen("session")
//    object Account : Screen("account")
//    object Profile: Screen("profile")
//    object Test: Screen("test")
//}