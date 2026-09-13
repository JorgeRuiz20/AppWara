package com.example.wara.domain.usecase.trabajador

import com.example.wara.core.result.Resource
import com.example.wara.domain.model.PaginatedResult
import com.example.wara.domain.model.Trabajador
import com.example.wara.domain.repository.TrabajadorRepository

class GetTrabajadoresUseCase(private val repository: TrabajadorRepository) {
    suspend operator fun invoke(
        dni: String? = null,
        pagina: Int = 1,
        tamanoPagina: Int = 10,
        soloActivos: Boolean = true,
        forceRefresh: Boolean = false
    ): Resource<PaginatedResult<Trabajador>> {
        val sanitizedDni = dni?.trim()?.ifEmpty { null }
        return repository.listarTrabajadores(sanitizedDni, pagina, tamanoPagina, soloActivos, forceRefresh)
    }
}
