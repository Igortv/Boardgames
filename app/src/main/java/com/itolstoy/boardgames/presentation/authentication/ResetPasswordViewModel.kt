package com.itolstoy.boardgames.presentation.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.usecase.authentication.FirebaseResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class ResetPasswordScreenState(
    var isLoading: Boolean = false,
    var isResetPasswordSuccessful: Boolean = false,
    var error: String = ""
)

enum class ResetPasswordStatus {
    DEFAULT, SUCCESS, FAILURE
}

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val firebaseResetPasswordUseCase: FirebaseResetPasswordUseCase
) : ViewModel() {

    val resetPasswordScreenState = mutableStateOf(ResetPasswordScreenState())
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                firebaseResetPasswordUseCase.invoke(email)
                    .onStart {
                        resetPasswordScreenState.value = ResetPasswordScreenState(isLoading = true)
                    }
                    .catch { result ->
                        resetPasswordScreenState.value = ResetPasswordScreenState(error = result.localizedMessage)
                    }
                    .collect {
                        resetPasswordScreenState.value = ResetPasswordScreenState(isResetPasswordSuccessful = true)
                    }
            } catch (e: Exception) {
                resetPasswordScreenState.value = ResetPasswordScreenState(error = e.localizedMessage)
            }
        }
    }
}