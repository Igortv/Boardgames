package com.itolstoy.boardgames.presentation.account

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.usecase.authentication.FirebaseSignOutUseCase
import com.itolstoy.boardgames.domain.usecase.authentication.GetCurrentUserUseCase
import com.itolstoy.boardgames.domain.usecase.profile.UpdateUserImageUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import com.itolstoy.boardgames.presentation.game.GameDetailScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class AccountScreenState(
    val isLoading: Boolean = false,
    val gamer: Gamer? = null,
    val isSignOut: Boolean = false,
    val isImageAddedToFirebase: Boolean = false,
    val error: String = "",
    var isNetworkError: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseSignOutUseCase: FirebaseSignOutUseCase,
    private val updateUserImageUseCase: UpdateUserImageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : BaseViewModel() {
    val accountScreenState = mutableStateOf(AccountScreenState())
    var gamer: Gamer? = null

    init {
        loadCurrentGamer()
    }

    fun signOut() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {
                    accountScreenState.value = AccountScreenState(isNetworkError = true)
                }
                is Exception -> {
                    accountScreenState.value = AccountScreenState(error = it.localizedMessage)
                }
            }
        }) {
            try {
                firebaseSignOutUseCase.invoke()
                    .onStart {
                        accountScreenState.value = AccountScreenState(isLoading = true)
                    }
                    .catch { result ->
                        accountScreenState.value = AccountScreenState(error = result.localizedMessage)
                    }
                    .collect {
                        accountScreenState.value = AccountScreenState(isSignOut = true)
                    }
            } catch (e: Exception) {
                accountScreenState.value = AccountScreenState(error = e.localizedMessage)
            }
        }
    }

    fun updateUserImageInFirebase(userId: String, imageUri: Uri) {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {
                    accountScreenState.value = AccountScreenState(isNetworkError = true)
                }
                is Exception -> {
                    accountScreenState.value = AccountScreenState(error = it.localizedMessage)
                }
            }
        }) {
            updateUserImageUseCase.invoke(userId, imageUri)
                .onStart {
                    accountScreenState.value = AccountScreenState(isLoading = true)
                }
                .onCompletion {
                    accountScreenState.value = AccountScreenState(isImageAddedToFirebase = true)
                }
                .collect()
        }
    }

    fun loadCurrentGamer() {
        viewModelScope.launch(handleException {
            when (it) {
                is CommunicationException -> {
                    accountScreenState.value = AccountScreenState(isNetworkError = true)
                }
                is Exception -> {
                    accountScreenState.value = AccountScreenState(error = it.localizedMessage)
                }
            }
        }) {
            getCurrentUserUseCase.invoke()
                .onStart {
                    accountScreenState.value = AccountScreenState(isLoading = true)
                }
                .collect {
                    gamer = it
                    accountScreenState.value = AccountScreenState(gamer = it)
                }
        }
    }
}