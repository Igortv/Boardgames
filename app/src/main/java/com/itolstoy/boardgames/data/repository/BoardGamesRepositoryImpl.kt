package com.itolstoy.boardgames.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.StorageException
import com.itolstoy.boardgames.data.remote.BoardGamesApi
import com.itolstoy.boardgames.data.remote.FirebaseCallExecutor
import com.itolstoy.boardgames.data.remote.dto.*
import com.itolstoy.boardgames.domain.common.Constants.COLLECTION_NAME_GAMERS
import com.itolstoy.boardgames.domain.common.Constants.COLLECTION_NAME_GAMES
import com.itolstoy.boardgames.domain.common.Constants.COLLECTION_NAME_SESSIONS
import com.itolstoy.boardgames.domain.common.Resource
import com.itolstoy.boardgames.domain.error.NoConnectivityException
import com.itolstoy.boardgames.domain.model.Game
import com.itolstoy.boardgames.domain.model.Gamer
import com.itolstoy.boardgames.domain.model.Score
import com.itolstoy.boardgames.domain.model.Session
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class BoardGamesRepositoryImpl @Inject constructor(
    private val boardGamesApi: BoardGamesApi,
    private val firestore: FirebaseFirestore,
    private val firebaseCallExecutor: FirebaseCallExecutor
) : BoardGamesRepository {

    override suspend fun getGames(): Resource<List<Game>> {
        return try {
            lateinit var result: Resource<List<Game>>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMES).get()
                    .addOnSuccessListener {
                        val games = it.toObjects<Game>()
                        if (games.isNotEmpty()) {
                            result = Resource.Success(games)
                        } else {
                            result = Resource.Success(emptyList())
                        }
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val response = boardGamesApi.getGames()
//        try {
//            if (response.isSuccessful) {
//                return if (response.body() == null) {
//                    Resource.Success(emptyList())
//                } else {
//                    val data = response.body()!!.toList().map { it.second.toGame() }
//                    Resource.Success(data)
//                }
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun createGame(game: Game): Resource<Unit> {
        return try {
            lateinit var result: Resource<Unit>

            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMES).document(game.gameId).set(game)
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
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection")
        }

//        val requestEntity = GameDto.from(game)
//        val response = boardGamesApi.createGame(requestEntity.id, requestEntity)
//        try {
//            if (response.isSuccessful) {
//                firestore.collection(COLLECTION_NAME_GAMES).document(game.gameId).set(game).await()
//                return Resource.Success(Unit)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun updateGame(game: Game): Resource<Unit> {
        return try {
            lateinit var result: Resource<Unit>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMES).document(game.gameId).set(game)
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val requestEntity = GameDto.from(game)
//        val response = boardGamesApi.updateGame(requestEntity.id, requestEntity)
//        try {
//            if (response.isSuccessful) {
//                return Resource.Success(Unit)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun getGameById(gameId: String): Resource<Game> {
        try {
            lateinit var result: Resource<Game>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMES).document(gameId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val game = documentSnapshot.toObject<Game>()
                        result = Resource.Success(game!!)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val response = boardGamesApi.getGameById(gameId)
//        try {
//            if (response.isSuccessful) {
//                val data = response.body()!!.toGame()
//                return Resource.Success(data)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection.")
//        }
    }

    override suspend fun deleteGame(gameId: String): Resource<Unit> {
        //val response = boardGamesApi.deleteGame(gameId)
        try {
            //if (response.isSuccessful) {
            lateinit var result: Resource<Unit>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMES).document(gameId).delete()
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
    }

    override suspend fun getGamers(): Resource<List<Gamer>> {
        try {
            lateinit var result: Resource<List<Gamer>>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).get()
                    .addOnSuccessListener {
                        val gamers = it.toObjects<GamerDto>().map { it.toGamer() }
                        if (gamers.isNotEmpty()) {
                            result = Resource.Success(gamers)
                        } else {
                            result = Resource.Success(emptyList())
                        }
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        try {
//            if (response.isSuccessful) {
//                if (response.body() == null) {
//                    return Resource.Success(emptyList())
//                } else {
//                    val data = response.body()!!.toList().map { it.second.toGamer() }
//                    return Resource.Success(data)
//                }
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun getSessionGamers(gamersId: List<String>): Resource<List<Gamer>> {
        try {
            lateinit var result: Resource<List<Gamer>>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).whereIn("gamerId", gamersId).get()
                    .addOnSuccessListener {
                        val players = it.toObjects<GamerDto>().map { it.toGamer() }
                        result = Resource.Success(players)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
    }

    override suspend fun createGamer(gamer: Gamer): Resource<Unit> {
        try {
            lateinit var result: Resource<Unit>
            val gamerDto = GamerDto.from(gamer)
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).document(gamer.gamerId).set(gamerDto)
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }.await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
        //val requestEntity = GamerDto.from(gamer)
        //val response = boardGamesApi.createGamer(requestEntity.id, requestEntity)
        /*try {
            if (response.isSuccessful) {
                return Resource.Success(Unit)
            } else {
                return Resource.Error("Response code: " + response.code())
            }
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }*/
    }

    override suspend fun updateGamer(gamer: Gamer): Resource<Unit> {
        return try {
            lateinit var result: Resource<Unit>
            val gamerDto = GamerDto.from(gamer)
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).document(gamer.gamerId).set(gamerDto)
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val requestEntity = GamerDto.from(gamer)
//        val response = boardGamesApi.updateGamer(requestEntity.id, requestEntity)
//        try {
//            if (response.isSuccessful) {
//                return Resource.Success(Unit)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun getGamerById(gamerId: String): Resource<Gamer> {
        try {
            lateinit var result: Resource<Gamer>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).document(gamerId).get()
                    .addOnSuccessListener {
                        val gamer = it.toObject<GamerDto>()!!.toGamer()
                        result = Resource.Success(gamer)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
//            val response = firestore.collection(COLLECTION_NAME_GAMES).document(gameId).get().addOnSuccessListener { documentSnapshot  ->
//                val game = documentSnapshot.toObject<Game>()
//                return@addOnSuccessListener Resource.Success(game)
//            }.addOnFailureListener {
//                return@addOnFailureListener Resource.Error("f")
//            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val response = boardGamesApi.getGamerById(gamerId)
//        try {
//            if (response.isSuccessful) {
//                val data = response.body()!!.toGamer()
//                return Resource.Success(data)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection.")
//        }
    }

    override suspend fun deleteGamer(gamerId: String): Resource<Unit> {
        try {
            lateinit var result: Resource<Unit>
            //if (response.isSuccessful) {
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_GAMERS).document(gamerId).delete()
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val response = boardGamesApi.deleteGamer(gamerId)
//        try {
//            if (response.isSuccessful) {
//                return Resource.Success(Unit)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun getSessions(): Resource<List<Session>> {
        return try {
            lateinit var result: Resource<List<Session>>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_SESSIONS).get()
                    .addOnSuccessListener {
                        val sessions = it.toObjects<SessionDto>().map { it.toSession() }
                        if (sessions.isNotEmpty()) {
                            result = Resource.Success(sessions)
                        } else {
                            result = Resource.Success(emptyList())
                        }
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val response = boardGamesApi.getSessions()
//        try {
//            if (response.isSuccessful) {
//                if (response.body() != null) {
//                    return Resource.Success(emptyList())
//                } else {
//                    val data = response.body()!!.toList().map { it.second.toSession() }
//                    return Resource.Success(data)
//                }
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun getGamerSessions(gamesId: List<String>): Resource<List<Session>> {
        try {
            lateinit var result: Resource<List<Session>>
            return if (gamesId.isNotEmpty()) {
                firebaseCallExecutor.firebaseCall {
                    firestore.collection(COLLECTION_NAME_SESSIONS).whereIn("gameId", gamesId).get()
                        .addOnSuccessListener {
                            val sessions = it.toObjects<SessionDto>().map { it.toSession() }
                            if (sessions.isNotEmpty()) {
                                result = Resource.Success(sessions)
                            } else {
                                result = Resource.Success(emptyList())
                            }
                        }
                        .addOnFailureListener {
                            result = Resource.Error(it.localizedMessage)
                        }.await()
                    result
                }
            } else {
                Resource.Success(emptyList())
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
    }

    override suspend fun getGameSessions(gameId: String): Resource<List<Session>> {
        try {
            lateinit var result: Resource<List<Session>>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_SESSIONS).whereEqualTo("gameId", gameId)
                    .get()
                    .addOnSuccessListener {
                        val sessions = it.toObjects<SessionDto>().map { it.toSession() }
                        if (sessions.isNotEmpty()) {
                            result = Resource.Success(sessions)
                        } else {
                            result = Resource.Success(emptyList())
                        }
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
    }

    override suspend fun createSession(
        gamers: List<Gamer>,
        session: Session,
        game: Game
    ): Resource<Unit> {
        try {
            lateinit var result: Resource<Unit>
            //val participants = gamers.mapIndexed { index, gamer ->  gamer.gamerId to index + 1 }.toMap()
            val participants =
                session.participants.entries.associate { (index, gamerId) -> gamerId to index + 1 }
            val ratings = mutableMapOf<String, Int>()
            val experience = mutableMapOf<String, Int>()
            var errorMessage = ""

            return firebaseCallExecutor.firebaseCall {
                gamers.forEach { gamer ->
                    ratings[gamer.gamerId] = gamer.averageScore
                    if (gamer.gamerExperience[game.gameId] != null) {
                        experience[gamer.gamerId] = gamer.gamerExperience[game.gameId]!! + 1
                    } else {
                        experience[gamer.gamerId] = 1
                    }
                }

                val newRatings =
                    Score.calculateRatings(game.complexity, ratings, participants, experience)
                firestore.runBatch { batch ->
                    gamers.forEach { gamer ->
                        gamer.averageScore = newRatings[gamer.gamerId]!!
                        if (gamer.gamerExperience[game.gameId] != null) {
                            gamer.gamerExperience[game.gameId] =
                                gamer.gamerExperience[game.gameId]!! + 1
                        } else {
                            gamer.gamerExperience[game.gameId] = 1
                        }
                        val gamerRef =
                            firestore.collection(COLLECTION_NAME_GAMERS).document(gamer.gamerId)
                        batch.set(gamerRef, gamer)
                    }
                    val sessionRef =
                        firestore.collection(COLLECTION_NAME_SESSIONS).document(session.sessionId)
                    val sessionDto = SessionDto.from(session)
                    batch.set(sessionRef, sessionDto)
                }.addOnSuccessListener {
                    result = Resource.Success(Unit)
                }.addOnFailureListener {
                    result = Resource.Error(it.localizedMessage)
                }.await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val requestEntity = SessionDto.from(session)
//        val response = boardGamesApi.createSession(requestEntity.id, requestEntity)
//        try {
//            if (response.isSuccessful) {
//                return Resource.Success(Unit)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun updateSession(session: Session): Resource<Unit> {
        return try {
            lateinit var result: Resource<Unit>
            val sessionDto = SessionDto.from(session)
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_SESSIONS).document(session.sessionId)
                    .set(sessionDto)
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val requestEntity = SessionDto.from(session)
//        val response = boardGamesApi.updateSession(requestEntity.id, requestEntity)
//        try {
//            if (response.isSuccessful) {
//                return Resource.Success(Unit)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }

    override suspend fun getSessionById(sessionId: String): Resource<Session> {
        try {
            lateinit var result: Resource<Session>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_SESSIONS).document(sessionId).get()
                    .addOnSuccessListener {
                        val session = it.toObject<SessionDto>()!!.toSession()
                        result = Resource.Success(session)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
//            val response = firestore.collection(COLLECTION_NAME_GAMES).document(gameId).get().addOnSuccessListener { documentSnapshot  ->
//                val game = documentSnapshot.toObject<Game>()
//                return@addOnSuccessListener Resource.Success(game)
//            }.addOnFailureListener {
//                return@addOnFailureListener Resource.Error("f")
//            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val response = boardGamesApi.getSessionById(sessionId)
//        try {
//            if (response.isSuccessful) {
//                val data = response.body()!!.toSession()
//                return Resource.Success(data)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection.")
//        }
    }

    override suspend fun deleteSession(sessionId: String): Resource<Unit> {
        try {
            lateinit var result: Resource<Unit>
            return firebaseCallExecutor.firebaseCall {
                firestore.collection(COLLECTION_NAME_SESSIONS).document(sessionId).delete()
                    .addOnSuccessListener {
                        result = Resource.Success(Unit)
                    }
                    .addOnFailureListener {
                        result = Resource.Error(it.localizedMessage)
                    }
                    .await()
                result
            }
        } catch (e: NoConnectivityException) {
            return Resource.NetworkError
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            return Resource.Error("Couldn't reach server. Check your internet connection")
        }
//        val response = boardGamesApi.deleteSession(sessionId)
//        try {
//            if (response.isSuccessful) {
//                return Resource.Success(Unit)
//            } else {
//                return Resource.Error("Response code: " + response.code())
//            }
//        } catch (e: HttpException) {
//            return Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
//        } catch (e: IOException) {
//            return Resource.Error("Couldn't reach server. Check your internet connection")
//        }
    }
}