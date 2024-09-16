package com.itolstoy.boardgames.domain.usecase.gamer

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateGamerUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
) {
    @Throws(Exception::class)
    operator fun invoke(gamer: Gamer): Flow<Unit> = flow {
        when (val result = boardGamesRepository.updateGamer(gamer)) {
            is Resource.Error -> {
                error(result.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}