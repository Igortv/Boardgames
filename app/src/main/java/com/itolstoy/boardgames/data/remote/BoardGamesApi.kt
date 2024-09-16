package com.itolstoy.boardgames.data.remote

import com.itolstoy.boardgames.data.remote.dto.GameDto
import com.itolstoy.boardgames.data.remote.dto.GamerDto
import com.itolstoy.boardgames.data.remote.dto.SessionDto
import retrofit2.Response
import retrofit2.http.*

interface BoardGamesApi {
    @GET("/gamers/.json")
    suspend fun getGamers(): Response<Map<String, GamerDto>>

    @GET("/gamers/{id}.json")
    suspend fun getGamerById(@Path("id") id: String): Response<GamerDto>

    @PUT("/gamers/{id}.json")
    suspend fun createGamer(@Path("id") id: String, @Body gamer: GamerDto): Response<Unit>

    @PUT("/gamers/{id}.json")
    suspend fun updateGamer(@Path("id") id: String?, @Body item: GamerDto): Response<Unit>

    @DELETE("/gamers/{id}.json")
    suspend fun deleteGamer(@Path("id") id: String?): Response<Unit>

    @GET("/games/.json")
    suspend fun getGames(): Response<Map<String, GameDto>>

    @GET("/games/{id}.json")
    suspend fun getGameById(@Path("id") id: String): Response<GameDto>

    @PUT("/games/{id}.json")
    suspend fun createGame(@Path("id") id: String, @Body game: GameDto): Response<Unit>

    @PUT("/games/{id}.json")
    suspend fun updateGame(@Path("id") id: String?, @Body item: GameDto): Response<Unit>

    @DELETE("/games/{id}.json")
    suspend fun deleteGame(@Path("id") id: String?): Response<Unit>

    @GET("/sessions/.json")
    suspend fun getSessions(): Response<Map<String, SessionDto>>

    @GET("/sessions/{id}.json")
    suspend fun getSessionById(@Path("id") id: String): Response<SessionDto>

    @PUT("/sessions/{id}.json")
    suspend fun createSession(@Path("id") id: String, @Body session: SessionDto): Response<Unit>

    @PUT("/sessions/{id}.json")
    suspend fun updateSession(@Path("id") id: String?, @Body item: SessionDto): Response<Unit>

    @DELETE("/sessions/{id}.json")
    suspend fun deleteSession(@Path("id") id: String?): Response<Unit>
}