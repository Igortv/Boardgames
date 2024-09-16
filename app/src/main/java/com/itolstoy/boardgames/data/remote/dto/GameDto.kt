package com.itolstoy.boardgames.data.remote.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.itolstoy.boardgames.domain.model.Game

data class GameDto(
    //@SerializedName("id")
    //@Expose
    val id: String,
    //@SerializedName("name")
    //@Expose
    val name: String,
    //@SerializedName("imageUrl")
    //@Expose
    val imageUrl: String,
    //@SerializedName("description")
    //@Expose
    val description: String,
    //@SerializedName("gameComplexity")
    //@Expose
    val gameComplexity: Float
) {
    companion object {
        fun from(game: Game): GameDto {
            return GameDto(
                id = game.gameId,
                name = game.name,
                imageUrl = game.imageUrl,
                description = game.description,
                gameComplexity = game.complexity
            )
        }
    }
}

fun GameDto.toGame(): Game {
    return Game(
        gameId = id,
        name = name,
        imageUrl = imageUrl,
        description = description,
        complexity = gameComplexity
    )
}