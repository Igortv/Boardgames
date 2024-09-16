package com.itolstoy.boardgames.domain.usecase.profile

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

//class GetImageFromFirestoreUseCase @Inject constructor(
//    private val imageRepository: ImageRepository
//) {
//    operator fun invoke(userId: String): Flow<String?> = flow {
//        val result = imageRepository.getUserImageFromFirestore(userId)
//        when (result) {
//            is Resource.Success -> {
//                emit(result.data)
//            }
//            is Resource.Error -> {
//                error(result.message)
//            }
//        }
//    }
//}