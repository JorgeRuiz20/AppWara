package com.example.wara.domain.usecase.auth

import com.example.wara.core.result.Resource
import com.example.wara.core.validation.PasswordValidator
import com.example.wara.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(nombreUsuario: String, password: String, confirmPassword: String): Resource<String> {
        val trimmedUser = nombreUsuario.trim()
        if (trimmedUser.length < 3 || trimmedUser.length > 50) {
            return Resource.Error("El nombre de usuario debe tener entre 3 y 50 caracteres.")
        }
        if (!PasswordValidator.isValid(password)) {
            return Resource.Error(PasswordValidator.ERROR_MESSAGE)
        }
        if (password != confirmPassword) {
            return Resource.Error("Las contraseñas no coinciden.")
        }
        return repository.register(trimmedUser, password)
    }
}
