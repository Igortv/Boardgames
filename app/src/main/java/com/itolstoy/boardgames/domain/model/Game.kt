package com.itolstoy.boardgames.domain.model

data class Game(val gameId: String = "", val name: String = "", val description: String = "", var imageUrl: String = "", val complexity: Float = 1.0f)
