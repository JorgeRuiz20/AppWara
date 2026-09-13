package com.example.wara.data.mapper

import com.example.wara.data.local.entity.TrabajadorEntity
import com.example.wara.data.remote.dto.TrabajadorDto
import com.example.wara.domain.model.Trabajador

object TrabajadorMapper {

    fun toDomain(dto: TrabajadorDto, activo: Boolean = true): Trabajador {
        return Trabajador(
            id = dto.id,
            nombre = dto.nombre,
            apellido = dto.apellido,
            dni = dto.dni,
            edad = dto.edad,
            activo = activo
        )
    }

    fun toDomain(entity: TrabajadorEntity): Trabajador {
        return Trabajador(
            id = entity.id,
            nombre = entity.nombre,
            apellido = entity.apellido,
            dni = entity.dni,
            edad = entity.edad,
            activo = entity.activo
        )
    }

    fun toEntity(dto: TrabajadorDto, activo: Boolean = true): TrabajadorEntity {
        return TrabajadorEntity(
            id = dto.id,
            nombre = dto.nombre,
            apellido = dto.apellido,
            dni = dto.dni,
            edad = dto.edad,
            activo = activo
        )
    }

    fun toEntity(domain: Trabajador): TrabajadorEntity {
        return TrabajadorEntity(
            id = domain.id,
            nombre = domain.nombre,
            apellido = domain.apellido,
            dni = domain.dni,
            edad = domain.edad,
            activo = domain.activo
        )
    }
}
