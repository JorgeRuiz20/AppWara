package com.example.wara.domain.usecase.trabajador

import com.example.wara.core.result.Resource
import com.example.wara.domain.repository.TrabajadorRepository

class EliminarTrabajadorUseCase(private val repository: TrabajadorRepository) {
    suspend operator fun invoke(id: Int): Resource<Unit> {
        return repository.eliminarTrabajador(id)
    }
}
