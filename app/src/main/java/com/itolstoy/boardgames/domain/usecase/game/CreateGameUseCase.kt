package com.itolstoy.boardgames.domain.usecase.game

import android.net.Uri
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateGameUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository,
    private val imageRepository: ImageRepository
) {
    @Throws(Exception::class)
    operator fun invoke(game: Game): Flow<Unit> = flow {
        if (game.imageUrl.isNotEmpty()) {
            val imageUri = Uri.parse(game.imageUrl)
            when (val response = imageRepository.addGameImageToFirebaseStorage(game.gameId, imageUri)) {
                is Resource.Success -> {
                    val downloadUrl = response.data.toString()
                    game.imageUrl = downloadUrl
                    //imageRepository.addGameImageToFirestore(game.gameId, downloadUrl)
                    val result = boardGamesRepository.createGame(game)
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
            when (val result = boardGamesRepository.createGame(game)) {
                is Resource.Error -> {
                    error(result.message)
                }
                is Resource.NetworkError -> {
                    throw CommunicationException()
                }
            }
        }
    }
}