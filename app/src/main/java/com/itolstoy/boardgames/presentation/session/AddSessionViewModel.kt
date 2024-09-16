package com.itolstoy.boardgames.presentation.session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.usecase.game.GetGamesUseCase
import com.itolstoy.boardgames.domain.usecase.gamer.GetGamersUseCase
import com.itolstoy.boardgames.domain.usecase.session.CreateSessionUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import com.itolstoy.boardgames.presentation.games.GamesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

data class AddSessionScreenState(
    var isLoading: Boolean = false,
    var sessionCreated: Boolean = false,
    var games: List<Game>? = null,
    var gamers: List<Gamer>? = null,
    var error: String = ""
)

@HiltViewModel
class AddSessionViewModel @Inject constructor(
    private val createSessionUseCase: CreateSessionUseCase,
    private val getGamesUseCase: GetGamesUseCase,
    private val getGamersUseCase: GetGamersUseCase
    ) : BaseViewModel() {
    val addSessionScreenState = mutableStateOf(AddSessionScreenState())

    var games: List<Game>? = null
    var gamers: List<Gamer>? = null

    init {
        getGames()
    }

    fun createSession(gamers: List<Gamer>, date: String, game: Game?) {
        viewModelScope.launch {
            try {
                if (date.isNotEmpty() && gamers.isNotEmpty() && game != null) {
                    val sessionId = UUID.randomUUID().toString()
                    val participants = gamers.mapIndexed { index, gamer -> index to gamer.gamerId }.toMap()
                    val winner = gamers.first()
                    val session = Session(sessionId, date, participants, game.gameId, game.name, winner.imageUrl)
                    createSessionUseCase.invoke(gamers, session, game)
                        .onStart {
                            addSessionScreenState.value = AddSessionScreenState(isLoading = true)
                        }
                        .catch { result ->
                            addSessionScreenState.value = AddSessionScreenState(error = result.localizedMessage)
                        }
                        .onCompletion {
                            addSessionScreenState.value = AddSessionScreenState(sessionCreated = true)
                        }
                        .collect()
                } else {
                    addSessionScreenState.value = AddSessionScreenState(error = "Invalid data")
                }
            } catch (e: Exception) {
                addSessionScreenState.value = AddSessionScreenState(error = e.localizedMessage)
            }
        }
    }

    private fun getGames() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {

                }
                is Exception -> {
                    addSessionScreenState.value = AddSessionScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getGamesUseCase.invoke()
                .onStart {
                    addSessionScreenState.value = AddSessionScreenState(isLoading = true)
                }
                .catch { result ->
                    addSessionScreenState.value = AddSessionScreenState(error = result.localizedMessage)
                }
                .collect { _games ->
                    games = _games
                    getGamers()
                }
        }
    }

    private fun getGamers() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {

                }
                is Exception -> {
                    addSessionScreenState.value = AddSessionScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getGamersUseCase.invoke()
                .onStart {
                    addSessionScreenState.value = AddSessionScreenState(isLoading = true)
                }
                .catch { result ->
                    addSessionScreenState.value = AddSessionScreenState(error = result.localizedMessage)
                }
                .collect { _gamers ->
                    gamers = _gamers
                    addSessionScreenState.value = AddSessionScreenState(games = games, gamers = gamers)
                }
        }
    }
}