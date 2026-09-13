package com.example.wara.domain.usecase.auth

import com.example.wara.core.result.Resource
import com.example.wara.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(nombreUsuario: String, password: String, confirmPassword: String): Resource<String> {
        val trimmedUser = nombreUsuario.trim()
        if (trimmedUser.length < 3 || trimmedUser.length > 50) {
            return Resource.Error("El nombre de usuario debe tener entre 3 y 50 caracteres.")
        }
        if (password.length < 8) {
            return Resource.Error("La contraseña debe tener al menos 8 caracteres.")
        }
        if (password != confirmPassword) {
            return Resource.Error("Las contraseñas no coinciden.")
        }
        return repository.register(trimmedUser, password)
    }
}
