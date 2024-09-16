package com.itolstoy.boardgames.presentation

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AppState(
    val scaffoldState: ScaffoldState,
    val coroutineScope: CoroutineScope,
    val navController: NavHostController
) {
    private var snackbarJob: Job? = null
    private var _snackbarScreen: String = ""
    private var _isSnackbarShown: Boolean = false

    val snackbarScreen: String
        get() = this._snackbarScreen

    val isSnackbarShown: Boolean
        get() = this._isSnackbarShown

    fun showSnackbar(message: String, duration: SnackbarDuration, action: () -> Unit) {
        cancelSnackbarJob()
        //if (snackbarJob == null) {
        snackbarJob = coroutineScope.launch {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = duration,
                actionLabel = "Retry"
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> {}
                SnackbarResult.ActionPerformed -> {
                    cancelSnackbarJob()
                    action.invoke()
                }
            }
            //}
        }
        _isSnackbarShown = true
        _snackbarScreen = navController.currentBackStackEntry?.destination?.route ?: ""
    }

    fun dismissSnackbar() {
        cancelSnackbarJob()
    }

    private fun cancelSnackbarJob() {
        snackbarJob?.let { job ->
            job.cancel()
            snackbarJob = Job()
        }
        _isSnackbarShown = false
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(
        snackbarHostState = remember {
            SnackbarHostState()
        },
        drawerState = rememberDrawerState(DrawerValue.Closed)
    ),
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
    ) = remember(scaffoldState, navController, coroutineScope){
    AppState(
        scaffoldState = scaffoldState,
        navController = navController,
        coroutineScope = coroutineScope)
    }