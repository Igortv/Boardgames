package com.itolstoy.boardgames.domain.usecase.game

import android.net.Uri
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateGameUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository,
    private val imageRepository: ImageRepository
) {
    @Throws(Exception::class)
    operator fun invoke(game: Game): Flow<Unit> = flow {
        val result = imageRepository.getGameImageFromFirestore(game.gameId)
        when (result) {
            is Resource.Success -> {
                val oldImageUrl = result.data
                val imageUrl = Uri.parse(game.imageUrl)

                if (game.imageUrl.isNotEmpty() && oldImageUrl != game.imageUrl) {
                    when (val response =
                        imageRepository.addGameImageToFirebaseStorage(game.gameId, imageUrl)) {
                        is Resource.Success -> {
                            val downloadUrl = response.data.toString()
                            game.imageUrl = downloadUrl
                            val result = boardGamesRepository.updateGame(game)
                            if (result is Resource.Error) {
                                error(result.message)
                            }
                        }
                        is Resource.Error -> {
                            error(response.message)
                        }
                        is Resource.NetworkError -> {
                            throw CommunicationException()
                        }
                    }
                } else {
                    val result = boardGamesRepository.updateGame(game)
                    if (result is Resource.Error) {
                        error(result.message)
                    }
                }
            }
            is Resource.Error-> {
                error(result.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }

    }
}