package com.itolstoy.boardgames.domain.usecase.session

import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
) {
    @Throws(Exception::class)
    operator fun invoke(gamers: List<Gamer>, session: Session, game: Game): Flow<Unit> = flow {
        when (val result = boardGamesRepository.createSession(gamers, session, game)) {
            is Resource.Error -> {
                error(result.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}