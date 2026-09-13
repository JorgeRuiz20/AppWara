package com.example.wara.data.repository

import com.example.wara.core.result.Resource
import com.example.wara.data.local.datastore.SessionDataStore
import com.example.wara.data.remote.api.WaraApiService
import com.example.wara.data.remote.dto.ApiErrorResponseDto
import com.example.wara.data.remote.dto.LoginRequestDto
import com.example.wara.data.remote.dto.RegisterRequestDto
import com.example.wara.domain.model.AuthSession
import com.example.wara.domain.model.Usuario
import com.example.wara.domain.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val apiService: WaraApiService,
    private val sessionDataStore: SessionDataStore,
    private val gson: Gson = Gson()
) : AuthRepository {

    override suspend fun login(nombreUsuario: String, password: String): Resource<AuthSession> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequestDto(nombreUsuario, password))
                if (response.isSuccessful && response.body()?.esExitoso == true) {
                    val body = response.body()!!
                    val token = body.token ?: ""
                    val user = body.nombreUsuario ?: nombreUsuario

                    sessionDataStore.saveSession(token = token, username = user)

                    Resource.Success(AuthSession(token = token, nombreUsuario = user))
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody()?.string())
                        ?: response.body()?.mensaje
                        ?: "Credenciales inválidas."
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de red al intentar iniciar sesión.", e)
            }
        }

    override suspend fun register(nombreUsuario: String, password: String): Resource<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(RegisterRequestDto(nombreUsuario, password))
                if (response.isSuccessful && response.body()?.esExitoso == true) {
                    Resource.Success(response.body()?.mensaje ?: "Usuario registrado exitosamente.")
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody()?.string())
                        ?: response.body()?.mensaje
                        ?: "No se pudo registrar el usuario."
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de red al intentar registrar usuario.", e)
            }
        }

    override suspend fun getUsuarioActual(): Resource<Usuario> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUsuarioActual()
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    sessionDataStore.updateUsername(dto.nombreUsuario)
                    Resource.Success(Usuario(id = dto.id, nombreUsuario = dto.nombreUsuario))
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody()?.string())
                        ?: "Sesión inválida o expirada."
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión.", e)
            }
        }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            sessionDataStore.clearSession()
        }
    }

    override fun getSavedToken(): Flow<String?> = sessionDataStore.tokenFlow

    override fun getSavedUsername(): Flow<String?> = sessionDataStore.usernameFlow

    private fun parseErrorMessage(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            val errorDto = gson.fromJson(json, ApiErrorResponseDto::class.java)
            if (!errorDto.mensaje.isNullOrBlank()) return errorDto.mensaje
            if (errorDto.errors != null && errorDto.errors.isNotEmpty()) {
                val firstKey = errorDto.errors.keys.first()
                return errorDto.errors[firstKey]?.firstOrNull() ?: errorDto.title
            }
            errorDto.title
        } catch (_: Exception) {
            null
        }
    }
}
