package com.itolstoy.boardgames.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.usecase.gamer.GetGamerByIdUseCase
import com.itolstoy.boardgames.domain.usecase.session.GetGamerSessionsUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class ProfileScreenState(
    var isLoading: Boolean = false,
    var gamer: Gamer? = null,
    var sessions: List<Session>? = emptyList(),
    var error: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getGamerByIdUseCase: GetGamerByIdUseCase,
    private val getGamerSessionsUseCase: GetGamerSessionsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val profileScreenState = mutableStateOf(ProfileScreenState())
    var gamer: Gamer? = null
    var sessions: List<Session>? = null
    private val gamerId = checkNotNull(savedStateHandle.get<String>("gamerId"))

    init {
        getGamer(gamerId)
    }

    private fun getGamerSessions(gamer: Gamer) {
        viewModelScope.launch {
            try {
                val gamesId = gamer.gamerExperience.keys.toList()
                getGamerSessionsUseCase.invoke(gamesId)
                    .catch { result ->
                        profileScreenState.value = ProfileScreenState(error = result.localizedMessage)
                    }
                    .collect { _sessions ->
                        sessions = _sessions
                        profileScreenState.value = ProfileScreenState(gamer = gamer, sessions = sessions)
                    }
            } catch (e: Exception) {
                profileScreenState.value = ProfileScreenState(error = e.localizedMessage)
            }
        }
    }

    fun getGamer(gamerId: String) {
        viewModelScope.launch {
            try {
                getGamerByIdUseCase.invoke(gamerId)
                    .onStart {
                        profileScreenState.value = ProfileScreenState(isLoading = true)
                    }
                    .catch { result ->
                        profileScreenState.value = ProfileScreenState(error = result.localizedMessage)
                    }
                    .collect { _gamer ->
                        gamer = _gamer
                        getGamerSessions(gamer!!)
                    }
            } catch (e: Exception) {
                profileScreenState.value = ProfileScreenState(error = e.localizedMessage)
            }
        }
    }
}