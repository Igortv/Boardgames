package com.itolstoy.boardgames.presentation.sidedrawer

import com.itolstoy.boardgames.presentation.Screens
import com.itolstoy.boardgames.R

sealed class NavDrawerItem(var route: String, var icon: Int, var title: String) {
    object Leaderboard : NavDrawerItem(Screens.LeaderboardScreen.route, R.drawable.abc_vector_test, "Leaderboard")
    object Games : NavDrawerItem(Screens.GamesScreen.route, R.drawable.abc_vector_test, "Games")
    object Sessions : NavDrawerItem(Screens.SessionsScreen.route, R.drawable.abc_vector_test, "Sessions")
}