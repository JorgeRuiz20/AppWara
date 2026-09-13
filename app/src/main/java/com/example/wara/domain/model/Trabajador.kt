package com.example.wara.domain.model

data class Trabajador(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val edad: Int,
    val activo: Boolean = true
) {
    val nombreCompleto: String
        get() = "$nombre $apellido".trim()

    val iniciales: String
        get() {
            val n = nombre.trim().firstOrNull()?.uppercaseChar()?.toString() ?: ""
            val a = apellido.trim().firstOrNull()?.uppercaseChar()?.toString() ?: ""
            return if (n.isNotEmpty() || a.isNotEmpty()) "$n$a" else "W"
        }
}
