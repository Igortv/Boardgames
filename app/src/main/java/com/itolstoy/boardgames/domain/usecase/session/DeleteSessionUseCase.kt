package com.itolstoy.boardgames.domain.usecase.session

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
) {
    @Throws(Exception::class)
    operator fun invoke(session: Session): Flow<Unit> = flow {
        when(val result = boardGamesRepository.deleteSession(session.sessionId)) {
            is Resource.Error -> {
                error(result.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}