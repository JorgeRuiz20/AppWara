package com.example.wara.di

import android.content.Context
import com.example.wara.core.constants.AppConstants
import com.example.wara.data.local.database.WaraDatabase
import com.example.wara.data.local.datastore.SessionDataStore
import com.example.wara.data.remote.api.WaraApiService
import com.example.wara.data.remote.interceptor.AuthInterceptor
import com.example.wara.data.repository.AuthRepositoryImpl
import com.example.wara.data.repository.TrabajadorRepositoryImpl
import com.example.wara.domain.repository.AuthRepository
import com.example.wara.domain.repository.TrabajadorRepository
import com.example.wara.domain.usecase.auth.*
import com.example.wara.domain.usecase.trabajador.*
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {

    // 1. Local Persistence
    val database: WaraDatabase by lazy {
        WaraDatabase.getInstance(context)
    }

    val sessionDataStore: SessionDataStore by lazy {
        SessionDataStore(context)
    }

    // 2. Network Client
    private val hostSelectionInterceptor: com.example.wara.data.remote.interceptor.HostSelectionInterceptor by lazy {
        com.example.wara.data.remote.interceptor.HostSelectionInterceptor(sessionDataStore)
    }

    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(sessionDataStore)
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(hostSelectionInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val gson = GsonBuilder().create()

    val apiService: WaraApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.DEFAULT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WaraApiService::class.java)
    }

    // 3. Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            apiService = apiService,
            sessionDataStore = sessionDataStore,
            gson = gson
        )
    }

    val trabajadorRepository: TrabajadorRepository by lazy {
        TrabajadorRepositoryImpl(
            apiService = apiService,
            dao = database.trabajadorDao(),
            gson = gson
        )
    }

    // 4. Use Cases
    val loginUseCase by lazy { LoginUseCase(authRepository) }
    val registerUseCase by lazy { RegisterUseCase(authRepository) }
    val getUsuarioActualUseCase by lazy { GetUsuarioActualUseCase(authRepository) }
    val logoutUseCase by lazy { LogoutUseCase(authRepository) }
    val getSavedSessionUseCase by lazy { GetSavedSessionUseCase(authRepository) }

    val getTrabajadoresUseCase by lazy { GetTrabajadoresUseCase(trabajadorRepository) }
    val getTrabajadorByIdUseCase by lazy { GetTrabajadorByIdUseCase(trabajadorRepository) }
    val crearTrabajadorUseCase by lazy { CrearTrabajadorUseCase(trabajadorRepository) }
    val actualizarTrabajadorUseCase by lazy { ActualizarTrabajadorUseCase(trabajadorRepository) }
    val eliminarTrabajadorUseCase by lazy { EliminarTrabajadorUseCase(trabajadorRepository) }
}
