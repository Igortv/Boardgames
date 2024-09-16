package com.itolstoy.boardgames.presentation.leaderboard

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.usecase.gamer.GetGamersUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import com.itolstoy.boardgames.presentation.games.GamesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class LeaderboardScreenState(
    var isLoading: Boolean = false,
    var gamers: List<Gamer> = emptyList(),
    var error: String = "",
    var isNetworkError: Boolean = false
)

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val getGamersUseCase: GetGamersUseCase
) : BaseViewModel() {
    var leaderboardScreenState = mutableStateOf(LeaderboardScreenState())
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    //    private val _isRefreshing = MutableStateFlow(false)
//
//    val isRefreshing: StateFlow<Boolean>
//        get() = _isRefreshing.asStateFlow()
    init {
        getLeaderBoardList()
    }

    fun getLeaderBoardList() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {
                    Log.d("leaderboardScreenState", "leaderboardScreenState.value = LeaderboardScreenState(isNetworkError = true)")
                    leaderboardScreenState.value = LeaderboardScreenState(isNetworkError = true)
                }
                is Exception -> {
                    leaderboardScreenState.value =
                        LeaderboardScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getGamersUseCase.invoke()
                .onStart {
                    Log.d("leaderboardScreenState", "leaderboardScreenState.value = LeaderboardScreenState(isLoading = true)")
                    leaderboardScreenState.value = LeaderboardScreenState(isLoading = true)
                }
                .collect { gamers ->
                    val sortedList = gamers.sortedByDescending { it.averageScore }
                    leaderboardScreenState.value = LeaderboardScreenState(gamers = sortedList)
                }
        }
    }

    fun refresh() {
        viewModelScope.launch(handleException {
            _isRefreshing.update { false }
            when (it) {
                is CommunicationException -> {
                    leaderboardScreenState.value = LeaderboardScreenState(isNetworkError = true)
                }
                is Exception -> {
                    leaderboardScreenState.value =
                        LeaderboardScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getGamersUseCase.invoke()
                .onStart {
                    _isRefreshing.update { true }
                }
                .collect { gamers ->
                    val sortedList = gamers.sortedByDescending { it.averageScore }
                    _isRefreshing.update { false }
                    leaderboardScreenState.value = LeaderboardScreenState(gamers = sortedList)
                }

        }
    }
}