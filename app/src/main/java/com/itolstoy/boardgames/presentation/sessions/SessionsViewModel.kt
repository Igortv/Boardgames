package com.itolstoy.boardgames.presentation.sessions

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.usecase.game.GetGameByIdUseCase
import com.itolstoy.boardgames.domain.usecase.gamer.GetGamerByIdUseCase
import com.itolstoy.boardgames.domain.usecase.preferences.GetAdminValueUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.domain.usecase.session.GetSessionsUseCase
import com.itolstoy.boardgames.presentation.arch.ViewState
import com.itolstoy.boardgames.presentation.games.GamesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class SessionsScreenState(
    val isLoading: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val error: String = "",
    var isNetworkError: Boolean = false
)
@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val getSessionsUseCase: GetSessionsUseCase,
    private val getAdminValueUseCase: GetAdminValueUseCase
) : BaseViewModel() {
    val sessionsScreenState = mutableStateOf(SessionsScreenState())
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        getSessions()
    }

    fun getSessions() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {
                    sessionsScreenState.value = SessionsScreenState(isNetworkError = true)
                }
                is Exception -> {
                    sessionsScreenState.value = SessionsScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getSessionsUseCase.invoke()
                .onStart {
                    sessionsScreenState.value = SessionsScreenState(isLoading = true)
                }
                .collect { sessions ->
                    sessionsScreenState.value = SessionsScreenState(sessions = sessions)
                }
        }
    }

    fun getAdminValue() = getAdminValueUseCase.invoke()

    fun refresh() {
        viewModelScope.launch(handleException {
            _isRefreshing.update { false }
            when (it) {
                is CommunicationException -> {
                    sessionsScreenState.value = SessionsScreenState(isNetworkError = true)
                }
                is Exception -> {
                    sessionsScreenState.value = SessionsScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getSessionsUseCase.invoke()
                .onStart {
                    _isRefreshing.update { true }
                }
                .collect { sessions ->
                    _isRefreshing.update { false }
                    sessionsScreenState.value = SessionsScreenState(sessions = sessions)
                }
        }
    }
}