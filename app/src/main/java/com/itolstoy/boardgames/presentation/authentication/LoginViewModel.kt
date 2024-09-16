package com.itolstoy.boardgames.presentation.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.usecase.authentication.FirebaseSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginScreenState(
    var isLoading: Boolean = false,
    var isSignInSuccessful: Boolean = false,
    var error: String = ""
)
enum class SignInStatus {
    DEFAULT, SUCCESS, FAILURE
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseSignInUseCase: FirebaseSignInUseCase
) : ViewModel() {
    val loginScreenState = mutableStateOf(LoginScreenState())

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                firebaseSignInUseCase.invoke(email, password)
                    .onStart {
                        loginScreenState.value = LoginScreenState(isLoading = true)
                    }
                    .catch { result ->
                        loginScreenState.value = LoginScreenState(error = result.localizedMessage)
                    }
                    .collect {
                        loginScreenState.value = LoginScreenState(isSignInSuccessful = true)
                    }
            } catch (e: Exception) {
                loginScreenState.value = LoginScreenState(error = e.localizedMessage)
            }
        }
    }
}