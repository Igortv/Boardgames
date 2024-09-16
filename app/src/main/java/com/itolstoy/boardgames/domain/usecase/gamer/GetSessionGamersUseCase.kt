package com.itolstoy.boardgames.domain.usecase.gamer

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSessionGamersUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
){
    @Throws(Exception::class)
    operator fun invoke(gamersId: List<String>): Flow<List<Gamer>> = flow {
        val result = boardGamesRepository.getSessionGamers(gamersId)
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