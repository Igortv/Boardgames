package com.itolstoy.boardgames.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itolstoy.boardgames.R
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.presentation.account.Account
import com.itolstoy.boardgames.presentation.connectivity_network.ConnectivityNetworkScreen
import com.itolstoy.boardgames.presentation.authentication.LoginScreen
import com.itolstoy.boardgames.presentation.authentication.ResetPasswordScreen
import com.itolstoy.boardgames.presentation.authentication.SignUpScreen
import com.itolstoy.boardgames.presentation.authentication.SplashScreen
import com.itolstoy.boardgames.presentation.game.AddGameScreen
import com.itolstoy.boardgames.presentation.game.EditGameScreen
import com.itolstoy.boardgames.presentation.game.GameDetailScreen
import com.itolstoy.boardgames.presentation.games.GamesScreen
import com.itolstoy.boardgames.presentation.leaderboard.LeaderboardScreen
import com.itolstoy.boardgames.presentation.profile.ProfileScreen
import com.itolstoy.boardgames.presentation.session.AddSessionScreen
import com.itolstoy.boardgames.presentation.session.EditSessionScreen
import com.itolstoy.boardgames.presentation.session.SessionDetailScreen
import com.itolstoy.boardgames.presentation.sessions.SessionsScreen
import com.itolstoy.boardgames.presentation.sidedrawer.NavDrawerItem
import com.itolstoy.boardgames.presentation.utils.network.ConnectivityManager
import com.itolstoy.boardgames.presentation.utils.network.ConnectivityObserver
import com.itolstoy.boardgames.presentation.utils.network.NetworkConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    @Inject
//    lateinit var connectivityManager: ConnectivityManager
//
//    override fun onStart() {
//        super.onStart()
//        connectivityManager.registerConnectionObserver(this)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        connectivityManager.unregisterConnectionObserver(this)
//    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appState: AppState = rememberAppState()
            val scaffoldState = appState.scaffoldState
            val scope = appState.coroutineScope
            val navController = appState.navController

            //val navController = rememberNavController()
            //viewModel.createGamer("gamer3", "imageUrl")

            var showTopBar by remember { mutableStateOf(true) }
            var topBarTitle by remember { mutableStateOf("") }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentScreen = navBackStackEntry?.destination?.route ?: ""

            showTopBar = when (navBackStackEntry?.destination?.route) {
                Screens.LeaderboardScreen.route -> true
                Screens.GamesScreen.route -> true
                Screens.SessionsScreen.route -> true
                else -> false
            }

            //dismiss snackbar when user navigates between screens
            if (appState.isSnackbarShown && appState.snackbarScreen != currentScreen) {
                appState.dismissSnackbar()
            }

            topBarTitle = navBackStackEntry?.destination?.route ?: ""
            
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = { if (showTopBar) TopBar(navController, scope = scope, scaffoldState = scaffoldState, topBarTitle) },
                drawerBackgroundColor = colorResource(id = R.color.colorPrimary),
                drawerContent = {
                    Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
                },
                drawerGesturesEnabled = scaffoldState.drawerState.isOpen
            ) {
                //val navController = rememberNavController()
                //SetupNavGraph(navController = navController)
                Navigation(navController = navController,
                showSnackbar = { message, duration, action ->
                    appState.showSnackbar(message, duration, action)
                })
                //homeNavGraph(navController)
            }
            //val navController = rememberNavController()
            //SetupNavGraph(navController = navController)
            //MainScreen(navController)
            //LoginScreen(navController = navController)
            //authNavGraph(navController)
        }
    }
}

//@Composable
//fun MainScreen(navController: NavHostController) {
//    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
//    val scope = rememberCoroutineScope()
//
//    //val navController = rememberNavController()
//    //viewModel.createGamer("gamer3", "imageUrl")
//
//    var showTopBar by remember { mutableStateOf(true) }
//    var topBarTitle by remember { mutableStateOf("") }
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//
//    showTopBar = when (navBackStackEntry?.destination?.route) {
//        Screens.LeaderboardScreen.route -> true
//        Screens.GamesScreen.route -> true
//        Screens.SessionsScreen.route -> true
//        else -> false
//    }
//
//    topBarTitle = navBackStackEntry?.destination?.route ?: ""
//
//    Scaffold(
//        scaffoldState = scaffoldState,
//        topBar = { if (showTopBar) TopBar(navController, scope = scope, scaffoldState = scaffoldState, topBarTitle) },
//        drawerBackgroundColor = colorResource(id = R.color.colorPrimary),
//        drawerContent = {
//            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
//        },
//        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
//    ) {
//        //val navController = rememberNavController()
//        //SetupNavGraph(navController = navController)
//        Navigation(navController = navController)
//        //homeNavGraph(navController)
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    MainScreen()
//}

@Composable
fun TopBar(navController: NavHostController, scope: CoroutineScope, scaffoldState: ScaffoldState, title: String) {
    TopAppBar(
        title = { Text(text = title, fontSize = 18.sp) },
        actions = {
                  IconButton(onClick = { navController.navigate(Screens.AccountScreen.route) }) {
                      Icon(Icons.Default.AccountBox, "Account")
                  }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, "")
            }
        },
        backgroundColor = colorResource(id = R.color.colorPrimary),
        contentColor = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    TopBar(rememberNavController(), scope = scope, scaffoldState = scaffoldState, "TopBar title")
}

@Composable
fun Drawer(scope: CoroutineScope,
           scaffoldState: ScaffoldState,
           navController: NavController) {
    val items = listOf(
        NavDrawerItem.Leaderboard,
        NavDrawerItem.Games,
        NavDrawerItem.Sessions
    )
    Column(modifier = Modifier
        .background(colorResource(id = R.color.colorPrimary))) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item->
            DrawerItem(item = item, selected = currentRoute == item.route, onItemClicked = {
                navController.navigate(item.route) {
                    navController.graph.startDestinationRoute?.let { route->
                        popUpTo(route) {
                            saveState = true
                        }
                    }

                    launchSingleTop = true
                    restoreState = true
                }

                scope.launch {
                    scaffoldState.drawerState.close()
                }
            })
        }
        Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
        Text(
            text = "Developed by Igor Tolstoy",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = androidx.compose.ui.Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val navController = rememberNavController()
    Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
}

@Composable
fun DrawerItem(item: NavDrawerItem, selected: Boolean, onItemClicked: (NavDrawerItem)->Unit) {
    val background = if (selected) R.color.colorPrimaryDark else android.R.color.transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClicked(item) })
            .height(45.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp)
    ) {
        Text(
            text = item.title,
            fontSize = 18.sp,
            color = Color.White
        )
        Spacer(modifier = androidx.compose.ui.Modifier.width(7.dp))
    }
}

@Preview(showBackground = false)
@Composable
fun DrawerItemPreview() {
    DrawerItem(item = NavDrawerItem.Leaderboard, selected = false, onItemClicked = {})
}

@Composable
fun Navigation(navController: NavHostController, showSnackbar: (String, SnackbarDuration: SnackbarDuration, action: () -> Unit) -> Unit) {
    NavHost(navController, startDestination = Screens.SplashScreen.route) {
//        composable(Screens.ConnectivityNetworkScreen.route) {
//            ConnectivityNetworkScreen()
//        }
        composable(Screens.LeaderboardScreen.route) {
            LeaderboardScreen(navController, showSnackbar)
        }
        composable(Screens.GamesScreen.route) {
            GamesScreen(navController, showSnackbar)
        }
        composable(Screens.SessionsScreen.route) {
            SessionsScreen(navController, showSnackbar)
        }
        composable(Screens.ProfileScreen.route + "/{gamerId}",
            arguments = listOf(
                navArgument("gamerId") { type = NavType.StringType }
            )) { backStackEntry ->
            backStackEntry.arguments?.getString("gamerId").let {
                ProfileScreen(navController)
//                navController.previousBackStackEntry?.arguments?.getParcelable<Gamer>("gamer")?.let { gamer ->
//                ProfileScreen(navController, gamer!!)
//            }
            }
        }
        composable(Screens.SessionDetailScreen.route + "/{sessionId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )) { backStackEntry ->
            backStackEntry.arguments?.getString("sessionId").let {
                SessionDetailScreen(navController)
            }
            //navController.previousBackStackEntry?.arguments?.getParcelable<Session>("session")?.let { session ->
            //}
        }
        composable(Screens.EditSessionScreen.route + "/{sessionId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )) { backStackEntry ->
            backStackEntry.arguments?.getString("sessionId").let {
                EditSessionScreen(navController)
            }
        }
        composable(Screens.AddSessionScreen.route) {
            AddSessionScreen(navController)
        }
        composable(Screens.GameScreen.route + "/{gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType }
            )) { backStackEntry ->
            backStackEntry.arguments?.getString("gameId").let {
                GameDetailScreen(navController)
            }
            //navController.previousBackStackEntry?.arguments?.getParcelable<Session>("session")?.let { session ->
            //}
//            navController.previousBackStackEntry?.arguments?.getParcelable<Game>("game")?.let { game ->
//                GameDetailScreen(navController, game!!)
//            }
        }
        composable(Screens.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.route) {
           LoginScreen(navController = navController)
        }
        composable(Screens.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(Screens.ResetPasswordScreen.route) {
            ResetPasswordScreen(navController = navController)
        }
        composable(Screens.AccountScreen.route) {
            Account(navController, showSnackbar)
        }
        composable(Screens.EditGameScreen.route + "/{gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType }
            )) { backStackEntry ->
            backStackEntry.arguments?.getString("gameId").let {
            //navController.previousBackStackEntry?.arguments?.getParcelable<Game>("game")?.let { game ->
                EditGameScreen(navController)
            }
        }
        composable(Screens.AddGameScreen.route) {
            AddGameScreen(navController)
        }
//        composable(Screen.Test.route) {
//            //val navHostController = rememberNavController()
//            TestScreen1()
//        }
    }
}