package com.itolstoy.boardgames.presentation.arch

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavController.navigate(route: String, params: Bundle?, builder: NavOptionsBuilder.() -> Unit = {}) {
    this.currentBackStackEntry?.arguments?.putAll(params)

    navigate(route, builder)
}