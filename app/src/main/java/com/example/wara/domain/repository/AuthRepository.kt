package com.example.wara.domain.repository

import com.example.wara.core.result.Resource
import com.example.wara.domain.model.AuthSession
import com.example.wara.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(nombreUsuario: String, password: String): Resource<AuthSession>
    suspend fun register(nombreUsuario: String, password: String): Resource<String>
    suspend fun getUsuarioActual(): Resource<Usuario>
    suspend fun logout()
    fun getSavedToken(): Flow<String?>
    fun getSavedUsername(): Flow<String?>
}
