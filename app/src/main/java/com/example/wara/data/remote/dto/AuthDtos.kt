package com.example.wara.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("nombreUsuario")
    val nombreUsuario: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponseDto(
    @SerializedName("esExitoso")
    val esExitoso: Boolean,
    @SerializedName("token")
    val token: String?,
    @SerializedName("nombreUsuario")
    val nombreUsuario: String?,
    @SerializedName("mensaje")
    val mensaje: String?
)

data class RegisterRequestDto(
    @SerializedName("nombreUsuario")
    val nombreUsuario: String,
    @SerializedName("password")
    val password: String
)

data class RegisterResponseDto(
    @SerializedName("esExitoso")
    val esExitoso: Boolean,
    @SerializedName("nombreUsuario")
    val nombreUsuario: String?,
    @SerializedName("mensaje")
    val mensaje: String?
)

data class UsuarioActualDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nombreUsuario")
    val nombreUsuario: String
)

data class ApiErrorResponseDto(
    @SerializedName("esExitoso")
    val esExitoso: Boolean? = false,
    @SerializedName("mensaje")
    val mensaje: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null
)
