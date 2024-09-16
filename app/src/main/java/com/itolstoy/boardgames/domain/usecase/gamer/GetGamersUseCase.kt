package com.itolstoy.boardgames.domain.usecase.gamer

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGamersUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
){
    @Throws(Exception::class)
    operator fun invoke(): Flow<List<Gamer>> = flow {
        val result = boardGamesRepository.getGamers()
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