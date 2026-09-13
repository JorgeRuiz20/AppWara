package com.example.wara.domain.model

data class Usuario(
    val id: Int,
    val nombreUsuario: String
)

data class AuthSession(
    val token: String,
    val nombreUsuario: String
)
