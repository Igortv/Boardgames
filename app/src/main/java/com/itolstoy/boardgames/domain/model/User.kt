package com.itolstoy.boardgames.domain.model

data class User(val userId: String = "", val name: String = "", val email: String = "", val imageUrl: String = "", val hasRoot: Boolean = false)
