package com.itolstoy.boardgames.domain.usecase.authentication

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.User
import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    @Throws(Exception::class)
    operator fun invoke() = flow {
        val result = authenticationRepository.getCurrentGamer()
        when (result) {
            is Resource.Success -> {
                emit(result.data)
            }
            is Resource.Error -> {
                error(result.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}