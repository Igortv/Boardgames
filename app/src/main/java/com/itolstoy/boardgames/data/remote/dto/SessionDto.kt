package com.itolstoy.boardgames.data.remote.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.itolstoy.boardgames.domain.model.Session

data class SessionDto(
    @SerializedName("sessionId")
    @Expose
    val sessionId: String = "",
    @SerializedName("date")
    @Expose
    val date: String = "",
    @SerializedName("participants")
    @Expose
    val participants: Map<String, String> = mapOf(),
    @SerializedName("gameId")
    @Expose
    val gameId: String = "",
    @SerializedName("gameName")
    @Expose
    val gameName: String = "",
    @SerializedName("winnerImageUrl")
    @Expose
    val winnerImageUrl: String = ""
) {
    companion object {
        fun from(session: Session): SessionDto {
            return SessionDto(
                sessionId = session.sessionId,
                date = session.date,
                participants = session.participants.mapKeys { it.key.toString() }.toMap(),
                gameId = session.gameId,
                gameName = session.gameName,
                winnerImageUrl = session.winnerImageUrl
            )
        }
    }
}

fun SessionDto.toSession(): Session {
    return Session(
        sessionId = sessionId,
        date = date,
        participants = participants.mapKeys { it.key.toInt() }.toMap(),
        gameId = gameId,
        gameName = gameName,
        winnerImageUrl = winnerImageUrl
    )
}