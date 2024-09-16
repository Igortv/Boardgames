package com.itolstoy.boardgames.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.itolstoy.boardgames.data.remote.FirebaseCallExecutor
import com.itolstoy.boardgames.domain.common.Constants.COLLECTION_NAME_GAMERS
import com.itolstoy.boardgames.domain.common.Constants.COLLECTION_NAME_GAMES
import com.itolstoy.boardgames.domain.common.Constants.FIRESTORE_IMAGE_URL
import com.itolstoy.boardgames.domain.common.Constants.GAME_IMAGES_FOLDER
import com.itolstoy.boardgames.domain.common.Constants.USER_IMAGES_FOLDER
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.NoConnectivityException
import com.itolstoy.boardgames.domain.repository.ImageRepository
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseStorageReference: StorageReference,
    private val firebaseCallExecutor: FirebaseCallExecutor
)  : ImageRepository {

    override suspend fun addUserImageToFirebaseStorage(imageUri: Uri): Resource<Uri> {
        try {
            val userId = auth.currentUser!!.uid
            val downloadUrl = firebaseCallExecutor.firebaseCall { firebaseStorageReference.child(USER_IMAGES_FOLDER + userId).putFile(imageUri).await()
                .storage.downloadUrl.await() }
            return Resource.Success(downloadUrl)
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun updateGamerImageInFirestore(downloadUrl: String): Resource<Unit> {
        try {
            lateinit var result: Resource<Unit>
            val gamerId = auth.currentUser!!.uid
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).document(gamerId).update(
                    mapOf(
                        FIRESTORE_IMAGE_URL to downloadUrl
                    )
                )
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun deleteImageFromStorage(imageUrl: String): Resource<Unit> {
        try {
            firebaseCallExecutor.firebaseCall { firebaseStorage.getReferenceFromUrl(imageUrl).delete().await() }
            return Resource.Success(Unit)
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun getGamerImageFromFirestore(gamerId: String): Resource<String?> {
        try {
            lateinit var result: Resource<String?>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).document(gamerId).get()
                    .addOnSuccessListener {
                        val url = it.getString(FIRESTORE_IMAGE_URL)
                        result = Resource.Success(url)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }.await()
                result
            }
        } catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun addGameImageToFirebaseStorage(gameId: String, imageUri: Uri): Resource<Uri> {
        try {
            val downloadUrl = firebaseCallExecutor.firebaseCall { firebaseStorageReference.child(GAME_IMAGES_FOLDER + gameId).putFile(imageUri).await()
                .storage.downloadUrl.await() }
            return Resource.Success(downloadUrl)
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }


//    override suspend fun addGameImageToFirestore(gameId: String, downloadUrl: String): Resource<Unit> {
//        try {
//            val obj = GameImage(gameId, downloadUrl)
//            firestore.collection(COLLECTION_NAME_GAMES).document(gameId).set(obj).await()
//            return Resource.Success(Unit)
//        } catch (e: Exception) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        }
//    }

//    override suspend fun updateGameImageInFirestore(gameId: String, downloadUrl: String): Resource<Unit> {
//        try {
//            firestore.collection(COLLECTION_NAME_GAMES).document(gameId).update(
//                mapOf(
//                    FIRESTORE_IMAGE_URL to downloadUrl
//                )
//            ).await()
//            return Resource.Success(Unit)
//        } catch (e: Exception) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        }
//    }

    override suspend fun getGameImageFromFirestore(gameId: String): Resource<String> {
        try {
            lateinit var result: Resource<String>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMES).document(gameId).get()
                    .addOnSuccessListener {
                        val url = it.getString(FIRESTORE_IMAGE_URL)
                        result = Resource.Success(url!!)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}