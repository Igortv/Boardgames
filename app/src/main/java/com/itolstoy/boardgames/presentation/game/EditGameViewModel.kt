package com.itolstoy.boardgames.presentation.game

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.usecase.game.DeleteGameUseCase
import com.itolstoy.boardgames.domain.usecase.game.GetGameByIdUseCase
import com.itolstoy.boardgames.domain.usecase.game.UpdateGameUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class EditGameScreenState(
    var isLoading: Boolean = false,
    var game: Game? = null,
    var gameUpdated: Boolean = false,
    var gameDeleted: Boolean = false,
    var error: String = ""
)

@HiltViewModel
class EditGameViewModel @Inject constructor(
    private val getGameByIdUseCase: GetGameByIdUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId = checkNotNull(savedStateHandle.get<String>("gameId"))
    val editGameScreenState = mutableStateOf(EditGameScreenState())

    init {
        getGame(gameId)
    }

    fun getGame(gameId: String) {
        viewModelScope.launch {
            try {
                getGameByIdUseCase.invoke(gameId)
                    .onStart {
                        editGameScreenState.value = EditGameScreenState(isLoading = true)
                    }
                    .catch { result ->
                        editGameScreenState.value = EditGameScreenState(error = result.localizedMessage)
                    }
                    .collect { game ->
                        editGameScreenState.value = EditGameScreenState(game = game)
                    }
            } catch (e: Exception) {
                editGameScreenState.value = EditGameScreenState(error = e.localizedMessage)
            }
        }
    }

    fun updateGame(gameId: String, name: String, description: String, imageUrl: String, gameComplexity: String) {
        viewModelScope.launch {
            try {
                val complexity = gameComplexity.toFloatOrNull()
                if (complexity != null) {
                    val game = Game(gameId, name, description, imageUrl, complexity)
                    updateGameUseCase.invoke(game)
                        .onStart {
                            editGameScreenState.value = EditGameScreenState(isLoading = true)
                        }
                        .catch { result ->
                            editGameScreenState.value = EditGameScreenState(error = result.localizedMessage)
                        }
                        .onCompletion {
                            editGameScreenState.value = EditGameScreenState(gameUpdated = true)
                        }
                        .collect()
                } else {
                    editGameScreenState.value = EditGameScreenState(error = "Invalid data")
                }
            } catch (e: Exception) {
                editGameScreenState.value = EditGameScreenState(error = e.localizedMessage)
            }
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            try {
                deleteGameUseCase.invoke(game)
                    .onStart {
                        editGameScreenState.value = EditGameScreenState(isLoading = true)
                    }
                    .catch { result ->
                        editGameScreenState.value = EditGameScreenState(error = result.localizedMessage)
                    }
                    .onCompletion {
                        editGameScreenState.value = EditGameScreenState(gameDeleted = true)
                    }
                    .collect()
            } catch (e: Exception) {
                editGameScreenState.value = EditGameScreenState(error = e.localizedMessage)
            }
        }
    }
}