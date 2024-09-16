package com.itolstoy.boardgames.presentation.session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.usecase.gamer.GetSessionGamersUseCase
import com.itolstoy.boardgames.domain.usecase.session.GetSessionByIdUseCase
import com.itolstoy.boardgames.presentation.arch.ViewState
import com.itolstoy.boardgames.presentation.game.GameDetailScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class SessionDetailScreenState(
    var isLoading: Boolean = false,
    var session: Session? = null,
    var gamers: List<Gamer> = emptyList(),
    var error: String = ""
)

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val getSessionGamersUseCase: GetSessionGamersUseCase,
    private val getSessionByIdUseCase: GetSessionByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val sessionDetailScreenState = mutableStateOf(SessionDetailScreenState())
    var session: Session? = null
    var gamers: List<Gamer>? = null
    private val sessionId = checkNotNull(savedStateHandle.get<String>("sessionId"))

    init {
        getSession(sessionId)
    }

    private fun loadSessionGamersInfo (participants: Map<Int, String>) {
        viewModelScope.launch {
            try {
                val gamersId = participants.values.toList()
                getSessionGamersUseCase.invoke(gamersId)
                    .catch { result ->
                        sessionDetailScreenState.value = SessionDetailScreenState(error = result.localizedMessage)
                    }
                    .collect { gamers ->
                        val gamePlayers = mutableListOf<Gamer>()
                        for (i in gamers.indices) {
                            gamePlayers.add(i, gamers.first { it.gamerId == participants[i] })
                        }
                        sessionDetailScreenState.value = SessionDetailScreenState(session = session, gamers = gamePlayers)
                    }
            } catch (e: Exception) {
                sessionDetailScreenState.value = SessionDetailScreenState(error = e.localizedMessage)
            }
        }
    }

    fun getSession(sessionId: String) {
        viewModelScope.launch {
            try {
                getSessionByIdUseCase.invoke(sessionId)
                    .onStart {
                        sessionDetailScreenState.value = SessionDetailScreenState(isLoading = true)
                    }
                    .catch { result ->
                        sessionDetailScreenState.value = SessionDetailScreenState(error = result.localizedMessage)
                    }
                    .collect { _session ->
                        session = _session
                        loadSessionGamersInfo(session!!.participants)
                    }
            } catch (e: Exception) {
                sessionDetailScreenState.value = SessionDetailScreenState(error = e.localizedMessage)
            }
        }
    }
}