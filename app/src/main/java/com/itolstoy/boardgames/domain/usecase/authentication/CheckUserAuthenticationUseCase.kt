package com.itolstoy.boardgames.domain.usecase.authentication

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckUserAuthenticationUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
    ) {
    @Throws(Exception::class)
    operator fun invoke(): Flow<Boolean> = flow {
        val authResult = authenticationRepository.isUserAuthenticatedInFirebase()
        if (authResult) {
            val gamerResult = authenticationRepository.getCurrentGamer()
            when (gamerResult) {
                is Resource.Success -> {
                    authenticationRepository.putUserAdminValue(gamerResult.data.hasRoot)
                    emit(true)
                }
                is Resource.Error -> {
                    error(gamerResult.message)
                }
                is Resource.NetworkError -> {
                    throw CommunicationException()
                }
            }
        } else {
            emit(false)
        }
    }
}