package com.itolstoy.boardgames.domain.model

import kotlin.math.sqrt

class Score {
    companion object {
        const val B = 0.01

        suspend fun calculateRatings(gameComplexity: Float, ratings: Map<String, Int>, participants: Map<String, Int>, gamersExperience: Map<String, Int>): Map<String, Int> {
            val wMap: MutableMap<String, Double> = mutableMapOf()
            val ratingMap: MutableMap<String, Int> = mutableMapOf()
            val gamerList = participants.keys.toList()

            gamerList.forEach { index->
                val gamerRating = ratings[index]!!.toDouble()
                val gamerExperience = gamersExperience[index]!!.toDouble()
                val gamerPlace = participants[index]!!.toDouble()

                var sum = 0.0
                participants.forEach { (id, currentGamerPlace) ->
                    val currentGamerRating = ratings[id]!!.toDouble()
                    val currentGamerExperience = gamersExperience[id]!!.toDouble()

                    sum =
                        ((sum + (currentGamerRating / gamerRating!! * (currentGamerExperience / (5 * gamerExperience!!)) * (1 - gamerRating / (gamerRating + currentGamerRating!!)) * sqrt(
                            (gamerPlace / currentGamerPlace!!).toFloat()
                        ) * (1 / gamerPlace))))
                }
                wMap[index] = sum
            }

            gamerList.forEach { index->
                val rating = (ratings[index]!! * (1 - B) + (wMap[index]!! / wMap.values.sum()) * (100 * gameComplexity + (ratings.values.sumOf { it * B }))).toInt()
                ratingMap[index] = rating
            }

            return ratingMap
        }
    }
}