package com.itolstoy.boardgames.domain.usecase.authentication

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import com.itolstoy.boardgames.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseSignUpUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
    ) {
    @Throws(Exception::class)
    operator fun invoke(name: String, email: String, password: String) = flow {
        val signUpResult = authenticationRepository.firebaseSignUp(name, email, password)
        when (signUpResult) {
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
                emit(signUpResult.data)
            }
            is Resource.Error -> {
                error(signUpResult.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}