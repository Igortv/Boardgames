package com.itolstoy.boardgames.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.itolstoy.boardgames.App
import com.itolstoy.boardgames.data.remote.BoardGamesApi
import com.itolstoy.boardgames.data.remote.FirebaseCallExecutor
import com.itolstoy.boardgames.data.remote.NetworkConnectionInterceptor
import com.itolstoy.boardgames.data.repository.AuthenticationRepositoryImpl
import com.itolstoy.boardgames.data.repository.BoardGamesRepositoryImpl
import com.itolstoy.boardgames.data.repository.ImageRepositoryImpl
import com.itolstoy.boardgames.domain.common.Constants
import com.itolstoy.boardgames.domain.error.NoConnectivityException
import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import com.itolstoy.boardgames.domain.repository.BoardGamesRepository
import com.itolstoy.boardgames.domain.repository.ImageRepository
import com.itolstoy.boardgames.domain.usecase.DoesNetworkHaveInternet
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBoardGamesApi(okHttpClient: OkHttpClient): BoardGamesApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BoardGamesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@NetworkInterceptorOkHttpClient networkInterceptorOkHttpClient: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(networkInterceptorOkHttpClient)
            .build()

    @NetworkInterceptorOkHttpClient
    @Provides
    @Singleton
    fun provideNetworkInterceptor(@ApplicationContext appContext: Context): Interceptor {
        return NetworkConnectionInterceptor(appContext)
    }

    @Provides
    @Singleton
    fun provideBoardGamesRepository(api: BoardGamesApi, firestore: FirebaseFirestore, firebaseCallExecutor: FirebaseCallExecutor): BoardGamesRepository {
        return BoardGamesRepositoryImpl(api, firestore, firebaseCallExecutor)
    }

    @Provides
    @Singleton
    fun provideBoardGamesAuthenticationRepository(
        sharedPreferences: SharedPreferences,
        auth: FirebaseAuth, firestore: FirebaseFirestore,
        firebaseCallExecutor: FirebaseCallExecutor): AuthenticationRepository {
        return AuthenticationRepositoryImpl(sharedPreferences, auth, firestore, firebaseCallExecutor)
    }

    @Provides
    @Singleton
    fun provideProfileImageRepository(auth: FirebaseAuth, firestore: FirebaseFirestore,
                                      firebaseStorage: FirebaseStorage,
                                      firebaseStorageReference: StorageReference,
                                      firebaseCallExecutor: FirebaseCallExecutor): ImageRepository {
        return ImageRepositoryImpl(auth, firestore, firebaseStorage, firebaseStorageReference, firebaseCallExecutor)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthentication(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        val settings = firestoreSettings {
            isPersistenceEnabled = false
        }
        firestore.firestoreSettings = settings
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseCallExecutor(
        @ApplicationContext context: Context,
        doesNetworkHaveInternet: DoesNetworkHaveInternet): FirebaseCallExecutor {
        return FirebaseCallExecutor(context, doesNetworkHaveInternet)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageReference(firebaseStorage: FirebaseStorage): StorageReference {
        return firebaseStorage.reference
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences((context as App).packageName, Context.MODE_PRIVATE)
    }
//    @Provides
//    @Singleton
//    fun provideSharedPreferencesRepository(sharedPreferences: SharedPreferences): SharedPreferencesRepository {
//        return SharedPreferencesRepositoryImpl(sharedPreferences)
//    }
    @Provides
    @Singleton
    fun provideDoesNetworkHaveInternet(): DoesNetworkHaveInternet {
        return DoesNetworkHaveInternet()
    }
}