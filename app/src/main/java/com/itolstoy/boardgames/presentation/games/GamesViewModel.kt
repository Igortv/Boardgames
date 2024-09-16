package com.itolstoy.boardgames.presentation.games

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.usecase.preferences.GetAdminValueUseCase
import com.itolstoy.boardgames.domain.usecase.game.GetGamesUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class GamesScreenState(
    var isLoading: Boolean = false,
    var games: List<Game>? = null,
    var error: String = "",
    var isNetworkError: Boolean = false
)

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase,
    private val getAdminValueUseCase: GetAdminValueUseCase
) : BaseViewModel() {
    val gamesScreenState = mutableStateOf(GamesScreenState())
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        getGames()
    }

    fun getGames() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {
                    gamesScreenState.value = GamesScreenState(isNetworkError = true)
                }
                is Exception -> {
                    gamesScreenState.value = GamesScreenState(error = it.localizedMessage)
                }
            }
        }) {
                getGamesUseCase.invoke()
                    .onStart {
                        gamesScreenState.value = GamesScreenState(isLoading = true)
                    }
//                    .catch { result ->
//                        when (result) {
//                            is CommunicationException -> {
//                                gamesScreenState.value = GamesScreenState(isNetworkError = true)
//                            }
//                            is Exception -> {
//                                gamesScreenState.value = GamesScreenState(error = result.localizedMessage)
//                            }
//                        }
//                        //gamesScreenState.value = GamesScreenState(error = result.localizedMessage)
//                    }
                    .collect { games ->
                        gamesScreenState.value = GamesScreenState(games = games)
                    }
        }
    }

    fun getAdminValue() = getAdminValueUseCase.invoke()

    fun refresh() {
        viewModelScope.launch(handleException {
            _isRefreshing.update { false }
            when (it) {
                is CommunicationException -> {
                    gamesScreenState.value = GamesScreenState(isNetworkError = true)
                }
                is Exception -> {
                    gamesScreenState.value = GamesScreenState(error = it.localizedMessage)
                }
            }
        }) {
            //try {
                getGamesUseCase.invoke()
                    .onStart {
                        _isRefreshing.update { true }
                    }
//                    .catch { result ->
//                        _isRefreshing.update { false }
//                        gamesScreenState.value = GamesScreenState(error = result.localizedMessage)
//                    }
                    .collect { games ->
                        _isRefreshing.update { false }
                        gamesScreenState.value = GamesScreenState(games = games)
                    }
//            } catch (e: Exception) {
//                _isRefreshing.update { false }
//                gamesScreenState.value = GamesScreenState(error = e.localizedMessage)
//            }
        }
    }
}