package com.itolstoy.sidedrawer.domain.repository

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.model.Upload

interface UploadRepository {
    suspend fun createUpload(upload: Upload): Resource<Unit>
    suspend fun updateUpload(upload: Upload): Resource<Unit>
    suspend fun getUploadById(uploadId: String): Resource<Upload>
    suspend fun deleteUpload(uploadId: String): Resource<Unit>
}