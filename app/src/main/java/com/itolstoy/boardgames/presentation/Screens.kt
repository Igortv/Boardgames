package com.itolstoy.boardgames.presentation

sealed class Screens(val route: String) {
    object SplashScreen : Screens("splash_screen")
    object ConnectivityNetworkScreen : Screens("connectivity_network")
    object LoginScreen : Screens("login_screen")
    object SignUpScreen : Screens("signup_screen")
    object ResetPasswordScreen : Screens("reset_password_screen")
    object LeaderboardScreen : Screens("Leaderboard")
    object GamesScreen : Screens("Games")
    object GameScreen : Screens("game")
    object AddGameScreen : Screens("add_game")
    object EditGameScreen : Screens("edit_game")
    object SessionsScreen : Screens("Sessions")
    object AddSessionScreen : Screens("add_session")
    object EditSessionScreen : Screens("edit_session")
    object SessionDetailScreen : Screens("session")
    object AccountScreen : Screens("account")
    object ProfileScreen: Screens("profile")
}