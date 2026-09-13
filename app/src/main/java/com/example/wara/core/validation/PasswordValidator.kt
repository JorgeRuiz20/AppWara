package com.example.wara.core.validation

object PasswordValidator {
    const val MIN_LENGTH = 8
    const val ERROR_MESSAGE = "La contraseña debe tener al menos 8 caracteres, incluir al menos una letra mayúscula y un símbolo (ej. @, !)."

    fun isValid(password: String): Boolean {
        if (password.length < MIN_LENGTH) return false
        val hasUpper = password.any { it.isUpperCase() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        return hasUpper && hasSymbol
    }
}
