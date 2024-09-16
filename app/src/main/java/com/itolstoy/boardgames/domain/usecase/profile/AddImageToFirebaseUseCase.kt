package com.itolstoy.boardgames.domain.usecase.profile

import android.net.Uri
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.repository.ProfileImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddImageToFirebaseUseCase @Inject constructor(
    private val profileImageRepository: ProfileImageRepository
) {
    @Throws(Exception::class)
    operator fun invoke(imageUri: Uri): Flow<Unit> = flow {
        when (val result = profileImageRepository.addImageToFirebaseStorage(imageUri)) {
            is Resource.Success -> {
                val uri = result.data.toString()
                val result = profileImageRepository.addImageToFirestore(uri)
                if (result is Resource.Error) {
                    error(result.message)
                }
                if (result is Resource.NetworkError) {
                    throw CommunicationException()
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