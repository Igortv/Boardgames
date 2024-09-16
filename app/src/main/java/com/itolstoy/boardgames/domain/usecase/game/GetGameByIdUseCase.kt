package com.itolstoy.boardgames.domain.usecase.game

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGameByIdUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
) {
    operator fun invoke(gameId: String): Flow<Game> = flow {
        val result = boardGamesRepository.getGameById(gameId)
        when(result) {
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