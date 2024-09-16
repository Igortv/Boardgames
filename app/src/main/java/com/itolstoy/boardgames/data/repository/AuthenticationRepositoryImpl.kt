package com.itolstoy.boardgames.data.repository

import android.content.SharedPreferences
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.itolstoy.boardgames.data.remote.FirebaseCallExecutor
import com.itolstoy.boardgames.data.remote.dto.GamerDto
import com.itolstoy.boardgames.domain.common.Constants
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.NoConnectivityException
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.User
import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseCallExecutor: FirebaseCallExecutor
)  : AuthenticationRepository {
    companion object {
        const val IS_ADMIN_PREF = "is_admin"
    }
    var operationSuccessful = false

    override fun isUserAuthenticatedInFirebase(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun getFirebaseAuthState(): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentGamer(): Resource<Gamer> {
        try {
            lateinit var result: Resource<Gamer>
            val gamerId = auth.currentUser?.uid!!
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(Constants.COLLECTION_NAME_GAMERS).document(gamerId).get()
                    .addOnSuccessListener {
                        val gamer = it.toObject<Gamer>()
                        result = Resource.Success(gamer!!)
                    }
                    .addOnFailureListener {
//                        if (it is FirebaseFirestoreException) {
//                            val errorCode = it.code
//                            if (errorCode == FirebaseFirestoreException.Code.UNAVAILABLE) {
//                                result = Resource.NetworkError
//                            } else {
//                                result = Resource.Error(it.localizedMessage)
//                            }
//                        } else {
                            result = Resource.Error(it.localizedMessage)
                        //}
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun firebaseSignIn(email: String, password: String): Resource<Unit> {
        return try {
            lateinit var result: Resource<Unit>
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                result = Resource.Success(Unit)
            }
            .addOnFailureListener {
                if(it is FirebaseNetworkException) {
                    result = Resource.NetworkError
                } else {
                    result = Resource.Error(it.localizedMessage)
                }
            }.await()

            result
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun firebaseSignOut(): Resource<Unit> {
        return try {
            firebaseCallExecutor.firebaseCall { auth.signOut() }
            Resource.Success(Unit)
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            lateinit var result: Resource<Unit>
            //firebaseCallExecutor.firebaseCall {
                auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    result = Resource.Success(Unit)
                }
                    .addOnFailureListener {
                        if(it is FirebaseNetworkException) {
                            result = Resource.NetworkError
                        } else {
                            result = Resource.Error(it.localizedMessage)
                        }
                    }.await()
            //}
            return result
        } /*catch (e: NoConnectivityException) {
            return Resource.NetworkError
        }*/ catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun firebaseSignUp(name: String, email: String, password: String): Resource<Unit> {
        try {
            lateinit var result: Resource<Unit>
            //firebaseCallExecutor.firebaseCall {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        operationSuccessful = true
                    }
                    .addOnFailureListener {
                        if(it is FirebaseNetworkException) {
                            result = Resource.NetworkError
                        } else {
                            result = Resource.Error(it.localizedMessage)
                        }
                    }.await()

                if (operationSuccessful) {
                    val gamerId = auth.currentUser?.uid!!
                    val obj = GamerDto.from(Gamer(gamerId, name, email))
                    firestore.collection(Constants.COLLECTION_NAME_GAMERS).document(gamerId)
                        .set(obj)
                        .addOnSuccessListener {
                            result = Resource.Success(Unit)
                        }
                        .addOnFailureListener {
                            if (it is FirebaseFirestoreException) {
                                val errorCode = it.code
                                if (errorCode == FirebaseFirestoreException.Code.UNAVAILABLE) {
                                    result = Resource.NetworkError
                                } else {
                                    result = Resource.Error(it.localizedMessage)
                                }
                            } else {
                                result = Resource.Error(it.localizedMessage)
                            }
                        }.await()
                }
            //}
            return result
        }/* catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } */catch (e: Exception) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override fun putUserAdminValue(value: Boolean) {
        sharedPreferences.edit().putBoolean(IS_ADMIN_PREF, value).apply()
    }

    override fun getUserAdminValue(): Boolean {
        return sharedPreferences.getBoolean(IS_ADMIN_PREF, false)
    }
}