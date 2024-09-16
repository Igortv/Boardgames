package com.itolstoy.boardgames.domain.usecase.authentication

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import com.itolstoy.boardgames.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseResetPasswordUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    @Throws(Exception::class)
    operator fun invoke(email: String) = flow {
        val resetPasswordResult = authenticationRepository.sendPasswordResetEmail(email)
        when (resetPasswordResult) {
            is Resource.Success -> {
                emit(resetPasswordResult.data)
            }
            is Resource.Error -> {
                error(resetPasswordResult.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}