package com.example.wara.data.repository

import com.example.wara.core.result.Resource
import com.example.wara.data.local.dao.TrabajadorDao
import com.example.wara.data.mapper.TrabajadorMapper
import com.example.wara.data.remote.api.WaraApiService
import com.example.wara.data.remote.dto.ActualizarTrabajadorRequestDto
import com.example.wara.data.remote.dto.ApiErrorResponseDto
import com.example.wara.data.remote.dto.CrearTrabajadorRequestDto
import com.example.wara.domain.model.PaginatedResult
import com.example.wara.domain.model.Trabajador
import com.example.wara.domain.repository.TrabajadorRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrabajadorRepositoryImpl(
    private val apiService: WaraApiService,
    private val dao: TrabajadorDao,
    private val gson: Gson = Gson()
) : TrabajadorRepository {

    override suspend fun listarTrabajadores(
        dni: String?,
        pagina: Int,
        tamanoPagina: Int,
        soloActivos: Boolean,
        forceRefresh: Boolean
    ): Resource<PaginatedResult<Trabajador>> = withContext(Dispatchers.IO) {
        try {
            // First attempt to fetch from backend
            val response = apiService.listarTrabajadores(dni = dni, pagina = pagina, tamanoPagina = tamanoPagina)
            if (response.isSuccessful && response.body() != null) {
                val pageDto = response.body()!!
                val domainItems = pageDto.items.map { TrabajadorMapper.toDomain(it) }

                // Cache to Room local DB
                val entities = pageDto.items.map { TrabajadorMapper.toEntity(it) }
                dao.insertTrabajadores(entities)

                val result = PaginatedResult(
                    items = domainItems,
                    pagina = pageDto.pagina,
                    tamanoPagina = pageDto.tamanoPagina,
                    totalRegistros = pageDto.totalRegistros,
                    totalPaginas = pageDto.totalPaginas
                )
                return@withContext Resource.Success(result)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    ?: "Error al obtener lista de trabajadores."
                // Try fallback to local database
                val localEntities = dao.getTrabajadores(dni, soloActivos)
                if (localEntities.isNotEmpty()) {
                    val localItems = localEntities.map { TrabajadorMapper.toDomain(it) }
                    return@withContext Resource.Success(
                        PaginatedResult(
                            items = localItems,
                            pagina = 1,
                            tamanoPagina = localItems.size,
                            totalRegistros = localItems.size,
                            totalPaginas = 1
                        )
                    )
                }
                return@withContext Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            // Network failure: fallback to Room local cache!
            val localEntities = dao.getTrabajadores(dni, soloActivos)
            if (localEntities.isNotEmpty()) {
                val localItems = localEntities.map { TrabajadorMapper.toDomain(it) }
                return@withContext Resource.Success(
                    PaginatedResult(
                        items = localItems,
                        pagina = 1,
                        tamanoPagina = localItems.size,
                        totalRegistros = localItems.size,
                        totalPaginas = 1
                    )
                )
            }
            return@withContext Resource.Error(
                e.localizedMessage ?: "Sin conexión y sin datos locales disponibles.",
                e
            )
        }
    }

    override suspend fun obtenerPorId(id: Int): Resource<Trabajador> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.obtenerTrabajadorPorId(id)
            if (response.isSuccessful && response.body() != null) {
                val domain = TrabajadorMapper.toDomain(response.body()!!)
                dao.insertTrabajador(TrabajadorMapper.toEntity(domain))
                Resource.Success(domain)
            } else {
                // Fallback to local
                val local = dao.getTrabajadorById(id)
                if (local != null) {
                    Resource.Success(TrabajadorMapper.toDomain(local))
                } else {
                    val msg = parseErrorMessage(response.errorBody()?.string())
                        ?: "No se encontró el trabajador con ID $id."
                    Resource.Error(msg)
                }
            }
        } catch (e: Exception) {
            val local = dao.getTrabajadorById(id)
            if (local != null) {
                Resource.Success(TrabajadorMapper.toDomain(local))
            } else {
                Resource.Error(e.localizedMessage ?: "Error al obtener trabajador.", e)
            }
        }
    }

    override suspend fun agregarTrabajador(
        nombre: String,
        apellido: String,
        dni: String,
        edad: Int
    ): Resource<Trabajador> = withContext(Dispatchers.IO) {
        try {
            val req = CrearTrabajadorRequestDto(nombre, apellido, dni, edad)
            val response = apiService.agregarTrabajador(req)
            if (response.isSuccessful && response.body()?.esExitoso == true) {
                val trabajadorDto = response.body()!!.trabajador
                if (trabajadorDto != null) {
                    val domain = TrabajadorMapper.toDomain(trabajadorDto)
                    dao.insertTrabajador(TrabajadorMapper.toEntity(domain))
                    Resource.Success(domain)
                } else {
                    Resource.Success(Trabajador(0, nombre, apellido, dni, edad))
                }
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    ?: response.body()?.mensaje
                    ?: "No se pudo registrar el trabajador."
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red al agregar trabajador.", e)
        }
    }

    override suspend fun actualizarTrabajador(
        id: Int,
        nombre: String,
        apellido: String,
        edad: Int
    ): Resource<Trabajador> = withContext(Dispatchers.IO) {
        try {
            val req = ActualizarTrabajadorRequestDto(nombre, apellido, edad)
            val response = apiService.actualizarTrabajador(id, req)
            if (response.isSuccessful && response.body()?.esExitoso == true) {
                val trabajadorDto = response.body()!!.trabajador
                if (trabajadorDto != null) {
                    val domain = TrabajadorMapper.toDomain(trabajadorDto)
                    dao.insertTrabajador(TrabajadorMapper.toEntity(domain))
                    Resource.Success(domain)
                } else {
                    val existing = dao.getTrabajadorById(id)
                    val dni = existing?.dni ?: ""
                    val updated = Trabajador(id, nombre, apellido, dni, edad)
                    dao.insertTrabajador(TrabajadorMapper.toEntity(updated))
                    Resource.Success(updated)
                }
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    ?: response.body()?.mensaje
                    ?: "No se pudo actualizar el trabajador."
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red al actualizar trabajador.", e)
        }
    }

    override suspend fun eliminarTrabajador(id: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.eliminarTrabajador(id)
            if (response.isSuccessful) {
                dao.softDeleteTrabajador(id)
                Resource.Success(Unit)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    ?: "No se pudo dar de baja al trabajador."
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red al dar de baja al trabajador.", e)
        }
    }

    private fun parseErrorMessage(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            val errorDto = gson.fromJson(json, ApiErrorResponseDto::class.java)
            if (!errorDto.mensaje.isNullOrBlank()) return errorDto.mensaje
            if (errorDto.errors != null && errorDto.errors.isNotEmpty()) {
                val firstKey = errorDto.errors.keys.first()
                return errorDto.errors[firstKey]?.firstOrNull() ?: errorDto.title
            }
            errorDto.title
        } catch (_: Exception) {
            null
        }
    }
}
