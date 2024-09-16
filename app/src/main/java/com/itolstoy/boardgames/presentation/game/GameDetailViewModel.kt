package com.itolstoy.boardgames.presentation.game

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.usecase.game.GetGameByIdUseCase
import com.itolstoy.boardgames.domain.usecase.session.GetGameSessionsUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.games.GamesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class GameDetailScreenState(
    var isLoading: Boolean = false,
    var game: Game? = null,
    var sessions: List<Session>? = null,
    var error: String = "",
    var isNetworkError: Boolean = false
)

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val getGameByIdUseCase: GetGameByIdUseCase,
    private val getGameSessionsUseCase: GetGameSessionsUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val gameDetailScreenState = mutableStateOf(GameDetailScreenState())
    private val gameId = checkNotNull(savedStateHandle.get<String>("gameId"))
    var game: Game? = null
    var sessions: List<Session>? = null

    init {
        getGame(gameId)
    }

    fun getGame(gameId: String) {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {
                    gameDetailScreenState.value = GameDetailScreenState(isNetworkError = true)
                }
                is Exception -> {
                    gameDetailScreenState.value = GameDetailScreenState(error = it.localizedMessage)
                }
            }
        }) {
            //try {
                getGameByIdUseCase.invoke(gameId)
                    .onStart {
                        gameDetailScreenState.value = GameDetailScreenState(isLoading = true)
                    }
//                    .catch { result ->
//                        gameDetailScreenState.value = GameDetailScreenState(error = result.localizedMessage)
//                    }
                    .collect { _game ->
                        game = _game
                        getGameSessions(game!!.gameId)
                    }
//            } catch (e: Exception) {
//                gameDetailScreenState.value = GameDetailScreenState(error = e.localizedMessage)
//            }
        }
    }

    private fun getGameSessions(gameId: String) {
        viewModelScope.launch {
            try {
                getGameSessionsUseCase.invoke(gameId)
                    .catch { result ->
                        gameDetailScreenState.value = GameDetailScreenState(error = result.localizedMessage)
                    }
                    .collect { _sessions ->
                        sessions = _sessions
                        gameDetailScreenState.value = GameDetailScreenState(game = game, sessions = sessions)
                    }
            } catch (e: Exception) {
                gameDetailScreenState.value = GameDetailScreenState(error = e.localizedMessage)
            }
        }
    }
}