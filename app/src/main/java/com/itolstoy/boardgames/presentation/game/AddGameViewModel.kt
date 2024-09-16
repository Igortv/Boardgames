package com.itolstoy.boardgames.presentation.game

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.usecase.game.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

data class AddGameScreenState(
    var isLoading: Boolean = false,
    var gameCreated: Boolean = false,
    var error: String = ""
)

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val createGameUseCase: CreateGameUseCase
) : ViewModel() {
    val addGameScreenState = mutableStateOf(AddGameScreenState())
    fun createGame(name: String, description: String, imageUrl: String, gameComplexity: String) {
        viewModelScope.launch {
            try {
                val complexity = gameComplexity.toFloatOrNull()
                if (name.isNotEmpty() && description.isNotEmpty() && complexity != null) {
                    val game = Game(
                        UUID.randomUUID().toString(),
                        name,
                        description,
                        imageUrl,
                        complexity
                    )
                    createGameUseCase.invoke(game)
                        .onStart {
                            addGameScreenState.value = AddGameScreenState(isLoading = true)
                        }
                        .catch { result ->
                            addGameScreenState.value =
                                AddGameScreenState(error = result.localizedMessage)
                        }
                        .onCompletion {
                            addGameScreenState.value = AddGameScreenState(gameCreated = true)
                        }
                        .collect()
                } else {
                    addGameScreenState.value = AddGameScreenState(error = "Invalid data")
                }
            } catch (e: Exception) {
                addGameScreenState.value = AddGameScreenState(error = e.localizedMessage)
            }
        }
    }
}