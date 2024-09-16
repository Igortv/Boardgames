package com.itolstoy.boardgames.domain.usecase.profile

import android.net.Uri
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.CommunicationException
import com.itolstoy.boardgames.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUserImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    @Throws(Exception::class)
    operator fun invoke(userId: String, imageUri: Uri): Flow<Unit> = flow {
        when (val oldImageResponse = imageRepository.getGamerImageFromFirestore(userId)) {
            is Resource.Success -> {
                if (oldImageResponse.data != null && oldImageResponse.data.isNotEmpty()) {
                    val oldImageUri = Uri.parse(oldImageResponse.data)
                    if (oldImageUri != imageUri) {
                        when (val response =
                            imageRepository.addUserImageToFirebaseStorage(imageUri)) {
                            is Resource.Success -> {
                                val downloadUrl = response.data.toString()
                                val result = imageRepository.updateGamerImageInFirestore(downloadUrl)
                                if (result is Resource.Error) {
                                    error(result.message)
                                }
                                if (result is Resource.NetworkError) {
                                    throw CommunicationException()
                                }
//                                val deleteOldImageResult = imageRepository.deleteImageFromStorage(oldImageUri.toString())
//                                if (deleteOldImageResult is Resource.Error) {
//                                    error(deleteOldImageResult.message)
//                                }
                            }
                            is Resource.Error -> {
                                error(response.message)
                            }
                            is Resource.NetworkError -> {
                                throw CommunicationException()
                            }
                        }
                    }
                } else {
                    when (val response =
                        imageRepository.addUserImageToFirebaseStorage(imageUri)) {
                        is Resource.Success -> {
                            val downloadUrl = response.data.toString()
                            val result = imageRepository.updateGamerImageInFirestore(downloadUrl)
                            if (result is Resource.Error) {
                                error(result.message)
                            }
                            if (result is Resource.NetworkError) {
                                throw CommunicationException()
                            }
                        }
                        is Resource.Error -> {
                            error(response.message)
                        }
                        is Resource.NetworkError -> {
                            throw CommunicationException()
                        }
                    }
                }
            }
            is Resource.Error -> {
                error(oldImageResponse.message)
            }
            is Resource.NetworkError -> {
                throw CommunicationException()
            }
        }
    }
}