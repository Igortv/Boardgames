package com.itolstoy.boardgames.presentation.authentication

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.usecase.DoesNetworkHaveInternet
import com.itolstoy.boardgames.domain.usecase.authentication.CheckUserAuthenticationUseCase
import com.itolstoy.boardgames.domain.usecase.authentication.FirebaseIsUserAuthenticatedUseCase
import com.itolstoy.boardgames.presentation.arch.BaseViewModel
import com.itolstoy.boardgames.presentation.arch.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.lang.Exception
import javax.inject.Inject

data class SplashScreenState(
    var isLoading: Boolean = false,
    var authenticationStatus: AuthenticationStatus = AuthenticationStatus.UNDEFINED,
    var noConnection: Boolean = false,
    var error: String = ""
)

enum class AuthenticationStatus {
    UNDEFINED, AUTHENTICATED, NOT_AUTHENTICATED
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseIsUserAuthenticatedUseCase: FirebaseIsUserAuthenticatedUseCase,
    private val checkUserAuthenticationUseCase: CheckUserAuthenticationUseCase,
    private val doesNetworkHaveInternet: DoesNetworkHaveInternet
) : ViewModel() {

    val splashScreenState = mutableStateOf(SplashScreenState())
    init {
        checkInternet()
    }

    fun isUserAuthenticated(): Boolean {
        return firebaseIsUserAuthenticatedUseCase.invoke()
    }

    fun sendNoInternet() {
        splashScreenState.value = SplashScreenState(noConnection = true)
    }

    fun checkInternet() {
        viewModelScope.launch {
            try {
                val hasInternet = CoroutineScope(Dispatchers.IO).async {
                    doesNetworkHaveInternet.execute()
                }.await()
                if (hasInternet) {
                    checkUserAuthentication()
                } else {
                    splashScreenState.value = SplashScreenState(noConnection = true)
                }
            } catch (e: Exception) {
                splashScreenState.value = SplashScreenState(error = e.localizedMessage)
            }
        }
    }

    fun checkUserAuthentication() {
        viewModelScope.launch {
            try {
                checkUserAuthenticationUseCase.invoke()
                        .onStart {
                            splashScreenState.value = SplashScreenState(isLoading = true)
                        }
//                        .catch { result ->
//                            splashScreenState.value = SplashScreenState(error = result.localizedMessage)
//                        }
                        .collect { isAuthenticated ->
                            if (isAuthenticated) {
                                splashScreenState.value = SplashScreenState(authenticationStatus = AuthenticationStatus.AUTHENTICATED)
                            } else {
                                splashScreenState.value = SplashScreenState(authenticationStatus = AuthenticationStatus.NOT_AUTHENTICATED)
                            }
                        }
            } catch (e: CommunicationException) {
                splashScreenState.value = SplashScreenState(noConnection = true)
            } catch (e: Exception) {
                splashScreenState.value = SplashScreenState(error = e.localizedMessage)
            }
        }
    }
}