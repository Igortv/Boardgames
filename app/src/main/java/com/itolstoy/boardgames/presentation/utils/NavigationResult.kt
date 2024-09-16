package com.itolstoy.boardgames.presentation.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

fun <T> NavController.getOnceResult(keyResult: String, onResult: (T) -> Unit){
    val valueScreenResult = currentBackStackEntry
        ?.savedStateHandle

    valueScreenResult?.get<T>(keyResult)?.let {
        onResult(it)

        currentBackStackEntry
            ?.savedStateHandle
            ?.remove<T>(keyResult)
    }
}

fun <T> NavController.setResult(keyResult: String, value: T){
    previousBackStackEntry?.savedStateHandle?.set(keyResult, value)
}