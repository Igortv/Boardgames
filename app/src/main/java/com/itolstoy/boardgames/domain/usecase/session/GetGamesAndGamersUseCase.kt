package com.itolstoy.boardgames.domain.usecase.session

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.model.GamesAndGamers
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGamesAndGamersUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository
) {
    operator fun invoke(): Flow<GamesAndGamers> = flow {
        val gamesResult = boardGamesRepository.getGames()
        when(gamesResult) {
            is Resource.Success-> {
                val games = gamesResult.data
                val gamersResult = boardGamesRepository.getGamers()
                when(gamersResult) {
                    is Resource.Success -> {
                        val gamers = gamersResult.data
                        val sessionInfo = GamesAndGamers(games, gamers)
                        emit(sessionInfo)
                    }
                    is Resource.Error -> {
                        error(gamersResult.message)
                    }
                }
            }
            is Resource.Error-> {
                error(gamesResult.message)
            }
        }
    }
}