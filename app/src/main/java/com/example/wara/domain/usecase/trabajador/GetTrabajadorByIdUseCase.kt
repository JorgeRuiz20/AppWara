package com.example.wara.domain.usecase.trabajador

import com.example.wara.core.result.Resource
import com.example.wara.domain.model.Trabajador
import com.example.wara.domain.repository.TrabajadorRepository

class GetTrabajadorByIdUseCase(private val repository: TrabajadorRepository) {
    suspend operator fun invoke(id: Int): Resource<Trabajador> {
        return repository.obtenerPorId(id)
    }
}
