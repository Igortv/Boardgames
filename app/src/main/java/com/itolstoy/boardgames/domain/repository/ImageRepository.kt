package com.itolstoy.boardgames.domain.repository

import android.net.Uri
import com.itolstoy.boardgames.domain.common.Resource

interface ImageRepository {
    suspend fun addUserImageToFirebaseStorage(imageUri: Uri): Resource<Uri>
    suspend fun updateGamerImageInFirestore(downloadUrl: String): Resource<Unit>
    suspend fun getGamerImageFromFirestore(gamerId: String): Resource<String?>

    suspend fun deleteImageFromStorage(imageUrl: String): Resource<Unit>

    suspend fun addGameImageToFirebaseStorage(gameId: String, imageUri: Uri): Resource<Uri>
//    suspend fun addGameImageToFirestore(gameId: String, downloadUrl: String): Resource<Unit>
//    suspend fun updateGameImageInFirestore(gameId: String, downloadUrl: String): Resource<Unit>
    suspend fun getGameImageFromFirestore(gameId: String): Resource<String?>
}