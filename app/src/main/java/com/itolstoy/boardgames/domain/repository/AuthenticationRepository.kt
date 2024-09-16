package com.itolstoy.boardgames.domain.repository

import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.model.Gamer

interface AuthenticationRepository {
    fun isUserAuthenticatedInFirebase(): Boolean
    suspend fun getFirebaseAuthState(): Resource<Boolean>
    suspend fun firebaseSignIn(email: String, password: String): Resource<Unit>
    suspend fun firebaseSignOut(): Resource<Unit>
    suspend fun firebaseSignUp(name: String, email: String, password: String): Resource<Unit>
    suspend fun getCurrentGamer(): Resource<Gamer>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    fun putUserAdminValue(value: Boolean)
    fun getUserAdminValue(): Boolean
}