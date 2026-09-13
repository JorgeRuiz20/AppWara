package com.example.wara.data.remote.api

import com.example.wara.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface WaraApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<RegisterResponseDto>

    @GET("api/auth/me")
    suspend fun getUsuarioActual(): Response<UsuarioActualDto>

    @GET("api/trabajadores")
    suspend fun listarTrabajadores(
        @Query("dni") dni: String?,
        @Query("pagina") pagina: Int,
        @Query("tamanoPagina") tamanoPagina: Int
    ): Response<ResultadoPaginadoDto<TrabajadorDto>>

    @GET("api/trabajadores/{id}")
    suspend fun obtenerTrabajadorPorId(
        @Path("id") id: Int
    ): Response<TrabajadorDto>

    @POST("api/trabajadores")
    suspend fun agregarTrabajador(
        @Body request: CrearTrabajadorRequestDto
    ): Response<CrearTrabajadorResponseDto>

    @PUT("api/trabajadores/{id}")
    suspend fun actualizarTrabajador(
        @Path("id") id: Int,
        @Body request: ActualizarTrabajadorRequestDto
    ): Response<CrearTrabajadorResponseDto>

    @DELETE("api/trabajadores/{id}")
    suspend fun eliminarTrabajador(
        @Path("id") id: Int
    ): Response<Unit>
}
