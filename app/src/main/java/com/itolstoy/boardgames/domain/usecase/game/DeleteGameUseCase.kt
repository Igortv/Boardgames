package com.itolstoy.boardgames.domain.usecase.game

import com.google.firebase.firestore.FirebaseFirestore
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteGameUseCase @Inject constructor(
    private val boardGamesRepository: BoardGamesRepository,
    private val imageRepository: ImageRepository
    ) {
    @Throws(Exception::class)
    operator fun invoke(game: Game): Flow<Unit> = flow {
        when (val result = boardGamesRepository.deleteGame(game.gameId)) {
            is Resource.Success -> {
                val deleteImageResult = imageRepository.deleteImageFromStorage(game.imageUrl)
                if (deleteImageResult is Resource.Error) {
                    error(deleteImageResult.message)
                }
            }
            is Resource.Error -> {
                error(result.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}