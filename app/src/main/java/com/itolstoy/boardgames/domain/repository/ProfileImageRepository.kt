package com.itolstoy.boardgames.domain.repository

import android.net.Uri
import com.itolstoy.boardgames.domain.common.Resource

interface ProfileImageRepository {
    suspend fun addImageToFirebaseStorage(imageUri: Uri): Resource<Uri>
    suspend fun addImageToFirestore(downloadUrl: String): Resource<Unit>
    suspend fun getImageFromFirestore(userId: String): Resource<String?>
}