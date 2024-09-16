package com.itolstoy.boardgames.presentation.session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.usecase.game.GetGamesUseCase
import com.itolstoy.boardgames.domain.usecase.gamer.GetGamersUseCase
import com.itolstoy.boardgames.domain.usecase.session.DeleteSessionUseCase
import com.itolstoy.boardgames.domain.usecase.session.GetSessionByIdUseCase
import com.itolstoy.boardgames.domain.usecase.session.UpdateSessionUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class EditSessionScreenState(
    var isLoading: Boolean = false,
    var session: Session? = null,
    var games: List<Game>? = null,
    val gamers: List<Gamer>? = null,
    var sessionUpdated: Boolean = false,
    var sessionDeleted: Boolean = false,
    var error: String = ""
)

@HiltViewModel
class EditSessionViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase,
    private val getGamersUseCase: GetGamersUseCase,
    private val getSessionByIdUseCase: GetSessionByIdUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val sessionId = checkNotNull(savedStateHandle.get<String>("sessionId"))
    val editSessionScreenState = mutableStateOf(EditSessionScreenState())

    var session: Session? = null
    var games: List<Game>? = null
    var gamers: List<Gamer>? = null

    init {
        getSession(sessionId)
    }

    private fun getSession(sessionId: String) {
        viewModelScope.launch {
            try {
                getSessionByIdUseCase.invoke(sessionId)
                    .onStart {
                        editSessionScreenState.value = EditSessionScreenState(isLoading = true)
                    }
                    .catch { result ->
                        editSessionScreenState.value = EditSessionScreenState(error = result.localizedMessage)
                    }
                    .collect { _session ->
                        session = _session
                        getGames()
                    }
            } catch (e: Exception) {
                editSessionScreenState.value = EditSessionScreenState(error = e.localizedMessage)
            }
        }
    }

    fun updateSession(sessionId: String, gamers: List<Gamer>, date: String, game: Game?) {
        viewModelScope.launch {
            try {
                if (date.isNotEmpty() && gamers.isNotEmpty() && game != null) {
                    val participants = gamers.mapIndexed { index, gamer -> index to gamer.gamerId }.toMap()
                    val winner = gamers.first()
                    val session = Session(sessionId, date, participants, game.gameId, game.name, winner.imageUrl)
                    updateSessionUseCase.invoke(session)
                        .onStart {
                            editSessionScreenState.value = EditSessionScreenState(isLoading = true)
                        }
                        .catch { result ->
                            editSessionScreenState.value = EditSessionScreenState(error = result.localizedMessage)
                        }
                        .onCompletion {
                            editSessionScreenState.value = EditSessionScreenState(sessionUpdated = true)
                        }
                        .collect() }
                else {
                    editSessionScreenState.value = EditSessionScreenState(error = "Invalid data")
                }
            } catch (e: Exception) {
                editSessionScreenState.value = EditSessionScreenState(error = e.localizedMessage)
            }
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            try {
                deleteSessionUseCase.invoke(session)
                    .onStart {
                        editSessionScreenState.value = EditSessionScreenState(isLoading = true)
                    }
                    .catch { result ->
                        editSessionScreenState.value = EditSessionScreenState(error = result.localizedMessage)
                    }
                    .onCompletion {
                        editSessionScreenState.value = EditSessionScreenState(sessionDeleted = true)
                    }
                    .collect()
            } catch (e: Exception) {
                editSessionScreenState.value = EditSessionScreenState(error = e.localizedMessage)
            }
        }
    }

    private fun getGames() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {

                }
                is Exception -> {
                    editSessionScreenState.value = EditSessionScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getGamesUseCase.invoke()
                .onStart {
                    editSessionScreenState.value = EditSessionScreenState(isLoading = true)
                }
                .catch { result ->
                    editSessionScreenState.value = EditSessionScreenState(error = result.localizedMessage)
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
                    editSessionScreenState.value = EditSessionScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getGamersUseCase.invoke()
                .onStart {
                    editSessionScreenState.value = EditSessionScreenState(isLoading = true)
                }
                .catch { result ->
                    editSessionScreenState.value = EditSessionScreenState(error = result.localizedMessage)
                }
                .collect { _gamers ->
                    gamers = _gamers
                    editSessionScreenState.value = EditSessionScreenState(session = session, games = games, gamers = gamers)
                }
        }
    }
}