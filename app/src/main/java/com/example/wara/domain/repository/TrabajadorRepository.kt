package com.example.wara.domain.repository

import com.example.wara.core.result.Resource
import com.example.wara.domain.model.PaginatedResult
import com.example.wara.domain.model.Trabajador
import kotlinx.coroutines.flow.Flow

interface TrabajadorRepository {
    suspend fun listarTrabajadores(
        dni: String? = null,
        pagina: Int = 1,
        tamanoPagina: Int = 10,
        soloActivos: Boolean = true,
        forceRefresh: Boolean = false
    ): Resource<PaginatedResult<Trabajador>>

    suspend fun obtenerPorId(id: Int): Resource<Trabajador>

    suspend fun agregarTrabajador(
        nombre: String,
        apellido: String,
        dni: String,
        edad: Int
    ): Resource<Trabajador>

    suspend fun actualizarTrabajador(
        id: Int,
        nombre: String,
        apellido: String,
        edad: Int
    ): Resource<Trabajador>

    suspend fun eliminarTrabajador(id: Int): Resource<Unit>
}
