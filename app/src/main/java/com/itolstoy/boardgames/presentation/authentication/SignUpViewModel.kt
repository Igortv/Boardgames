package com.itolstoy.boardgames.presentation.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.usecase.authentication.FirebaseSignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

data class SignUpScreenState(
    var isLoading: Boolean = false,
    var isSignUp: Boolean = false,
    var error: String = ""
)

//enum class SignUpStatus {
//    DEFAULT, SUCCESS, FAILURE
//}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseSignUpUseCase: FirebaseSignUpUseCase
) : ViewModel() {
    val signUpScreenState = mutableStateOf(SignUpScreenState())
    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    firebaseSignUpUseCase.invoke(name, email, password)
                        .onStart {
                            signUpScreenState.value = SignUpScreenState(isLoading = true)
                        }
                        .catch { result ->
                            signUpScreenState.value =
                                SignUpScreenState(error = result.localizedMessage)
                        }
                        .collect {
                            signUpScreenState.value =
                                SignUpScreenState(isSignUp = true)

                        }
                } else {
                    signUpScreenState.value =
                        SignUpScreenState(error = "Invalid data")
                }
            } catch (e: Exception) {
                signUpScreenState.value = SignUpScreenState(error = e.localizedMessage)
            }
        }
    }
}