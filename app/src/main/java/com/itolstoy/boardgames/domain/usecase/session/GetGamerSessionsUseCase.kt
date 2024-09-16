package com.itolstoy.boardgames.domain.usecase.session

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGamerSessionsUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
){
    @Throws(Exception::class)
    operator fun invoke(gamesId: List<String>): Flow<List<Session>> = flow {
        val result = boardGamesRepository.getGamerSessions(gamesId)
        when (result) {
            is Resource.Success-> {
                emit(result.data)
            }
            is Resource.Error-> {
                error(result.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}