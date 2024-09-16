package com.itolstoy.boardgames.data.remote.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.itolstoy.boardgames.domain.model.Gamer

data class GamerDto(
    @SerializedName("gamerId")
    @Expose
    val gamerId: String = "",
    @SerializedName("name")
    @Expose
    val name: String = "",
    @SerializedName("imageUrl")
    @Expose
    val imageUrl: String = "",
    @Expose
    @SerializedName("gamerExperience")
    val gamerExperience: Map<String, Int> = mapOf(),
    @SerializedName("averageScore")
    @Expose
    val averageScore: Int = 1000
) {
    companion object {
        fun from(gamer: Gamer): GamerDto {
            return GamerDto(
                gamerId = gamer.gamerId,
                name = gamer.name,
                imageUrl = gamer.imageUrl,
                gamerExperience = gamer.gamerExperience,//.mapValues { it.value.toString() },
                averageScore = gamer.averageScore
            )
        }
    }
}

fun GamerDto.toGamer(): Gamer {
    return Gamer(
        gamerId = gamerId,
        name = name,
        imageUrl = imageUrl,
        gamerExperience = gamerExperience/*.mapValues { it.value.toInt() }*/.toMutableMap(),
        averageScore = averageScore
    )
}