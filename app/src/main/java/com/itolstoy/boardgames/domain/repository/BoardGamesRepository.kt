package com.itolstoy.boardgames.domain.repository

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Session

interface BoardGamesRepository {
    suspend fun getGames(): Resource<List<Game>>
    suspend fun createGame(game: Game): Resource<Unit>
    suspend fun updateGame(game: Game): Resource<Unit>
    suspend fun getGameById(gameId: String): Resource<Game>
    suspend fun deleteGame(gameId: String): Resource<Unit>

    suspend fun getGamers(): Resource<List<Gamer>>
    suspend fun getSessionGamers(gamersId: List<String>): Resource<List<Gamer>>
    suspend fun createGamer(gamer: Gamer): Resource<Unit>
    suspend fun updateGamer(gamer: Gamer): Resource<Unit>
    suspend fun getGamerById(gamerId: String): Resource<Gamer>
    suspend fun deleteGamer(gamerId: String): Resource<Unit>

    suspend fun getSessions(): Resource<List<Session>>
    suspend fun getGamerSessions(gamesId: List<String>): Resource<List<Session>>
    suspend fun getGameSessions(gameId: String): Resource<List<Session>>
    suspend fun createSession(gamers: List<Gamer>, session: Session, game: Game): Resource<Unit>
    suspend fun updateSession(session: Session): Resource<Unit>
    suspend fun getSessionById(sessionId: String): Resource<Session>
    suspend fun deleteSession(sessionId: String): Resource<Unit>
}