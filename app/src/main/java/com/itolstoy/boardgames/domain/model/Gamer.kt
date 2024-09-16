package com.itolstoy.boardgames.domain.model

data class Gamer(
    val gamerId: String = "",
    val name: String = "",
    val email: String = "",
    val imageUrl: String = "",
    var gamerExperience: MutableMap<String, Int> = mutableMapOf(),
    var averageScore: Int = 1000,
    val hasRoot: Boolean = false
)
