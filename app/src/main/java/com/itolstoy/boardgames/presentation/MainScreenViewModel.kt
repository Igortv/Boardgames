package com.itolstoy.boardgames.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.usecase.authentication.GetCurrentUserUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainScreenViewState : ViewState() {
    class UserLoaded(val gamer: Gamer) : MainScreenViewState()
    object Loading : MainScreenViewState()
    class Error(val message: String) : MainScreenViewState()
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _viewState = mutableStateOf<MainScreenViewState>(MainScreenViewState.Loading)
    val viewState: State<MainScreenViewState> = _viewState

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                getCurrentUserUseCase.invoke()
                    .onStart {
                        _viewState.value = MainScreenViewState.Loading
                    }
                    .catch { result ->
                        _viewState.value = MainScreenViewState.Error(result.localizedMessage)
                    }
                    .collect {
                        _viewState.value = MainScreenViewState.UserLoaded(it)
                    }
            } catch (e: Exception) {
                _viewState.value = MainScreenViewState.Error(e.localizedMessage)
            }
        }
    }
}