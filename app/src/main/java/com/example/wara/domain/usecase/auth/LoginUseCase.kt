package com.example.wara.domain.usecase.auth

import com.example.wara.core.result.Resource
import com.example.wara.core.validation.PasswordValidator
import com.example.wara.domain.model.AuthSession
import com.example.wara.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(nombreUsuario: String, password: String): Resource<AuthSession> {
        val trimmedUser = nombreUsuario.trim()
        if (trimmedUser.isEmpty()) {
            return Resource.Error("El usuario es obligatorio.")
        }
        if (password.isEmpty()) {
            return Resource.Error("La contraseña es obligatoria.")
        }
        if (!PasswordValidator.isValid(password)) {
            return Resource.Error(PasswordValidator.ERROR_MESSAGE)
        }
        return repository.login(trimmedUser, password)
    }
}
