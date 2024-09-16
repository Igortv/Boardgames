package com.itolstoy.boardgames.domain.usecase.game

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

//class GetGameImageByIdUseCase @Inject constructor(
//    private val imageRepository: ImageRepository
//) {
//    operator fun invoke(gameId: String): Flow<String?> = flow {
//        val result = imageRepository.getGameImageFromFirestore(gameId)
//        when(result) {
//            is Resource.Success-> {
//                emit(result.data)
//            }
//            is Resource.Error-> {
//                error(result.message)
//            }
//        }
//    }
//}