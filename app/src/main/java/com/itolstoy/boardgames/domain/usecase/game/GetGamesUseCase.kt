package com.itolstoy.boardgames.domain.usecase.game

import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetGamesUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
) {
    @Throws(Exception::class)
    operator fun invoke(): Flow<List<Game>> = flow {
        val result = boardGamesRepository.getGames()
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