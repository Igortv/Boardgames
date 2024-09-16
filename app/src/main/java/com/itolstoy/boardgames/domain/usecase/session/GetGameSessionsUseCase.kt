package com.itolstoy.boardgames.domain.usecase.session

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGameSessionsUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
){
    @Throws(Exception::class)
    operator fun invoke(gameId: String): Flow<List<Session>> = flow {
        val result = boardGamesRepository.getGameSessions(gameId)
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