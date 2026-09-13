package com.example.wara.domain.usecase.auth

import com.example.wara.core.result.Resource
import com.example.wara.domain.model.Usuario
import com.example.wara.domain.repository.AuthRepository

class GetUsuarioActualUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Resource<Usuario> {
        return repository.getUsuarioActual()
    }
}
