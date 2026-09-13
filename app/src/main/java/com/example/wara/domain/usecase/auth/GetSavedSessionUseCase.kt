package com.example.wara.domain.usecase.auth

import com.example.wara.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSavedSessionUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Pair<String?, String?>> {
        return repository.getSavedToken().combine(repository.getSavedUsername()) { token, username ->
            Pair(token, username)
        }
    }
}
