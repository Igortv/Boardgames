package com.itolstoy.boardgames.domain.model

data class Session(val sessionId: String = "",
                   val date: String = "",
                   val participants: Map<Int, String> = mapOf(),
                   val gameId: String = "",
                   val gameName: String = "",
                   val winnerImageUrl: String = "")
