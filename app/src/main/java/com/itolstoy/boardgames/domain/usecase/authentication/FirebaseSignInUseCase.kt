package com.itolstoy.boardgames.domain.usecase.authentication

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import com.itolstoy.boardgames.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseSignInUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    @Throws(Exception::class)
    operator fun invoke(email: String, password: String) = flow {
        val signInResult = authenticationRepository.firebaseSignIn(email, password)
        when (signInResult) {
            is Resource.Success -> {
                val userResult = authenticationRepository.getCurrentGamer()
                when (userResult) {
                    is Resource.Success -> {
                        authenticationRepository.putUserAdminValue(userResult.data.hasRoot)
                    }
                    is Resource.Error -> {
                        error(userResult.message)
                    }
                }
                emit(signInResult.data)
            }
            is Resource.Error -> {
                error(signInResult.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}