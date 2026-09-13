package com.example.wara.domain

import com.example.wara.data.local.entity.TrabajadorEntity
import com.example.wara.data.mapper.TrabajadorMapper
import com.example.wara.data.remote.dto.TrabajadorDto
import com.example.wara.domain.model.Trabajador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrabajadorMapperTest {

    @Test
    fun `toDomain maps DTO correctly`() {
        val dto = TrabajadorDto(
            id = 1,
            nombre = "Rosa",
            apellido = "Castillo Díaz",
            dni = "76789012",
            edad = 52
        )
        val domain = TrabajadorMapper.toDomain(dto, activo = true)

        assertEquals(1, domain.id)
        assertEquals("Rosa", domain.nombre)
        assertEquals("Castillo Díaz", domain.apellido)
        assertEquals("76789012", domain.dni)
        assertEquals(52, domain.edad)
        assertTrue(domain.activo)
        assertEquals("Rosa Castillo Díaz", domain.nombreCompleto)
        assertEquals("RC", domain.iniciales)
    }

    @Test
    fun `toEntity maps Domain correctly`() {
        val domain = Trabajador(
            id = 2,
            nombre = "Carlos",
            apellido = "Fernández",
            dni = "73456789",
            edad = 41,
            activo = true
        )
        val entity = TrabajadorMapper.toEntity(domain)

        assertEquals(2, entity.id)
        assertEquals("Carlos", entity.nombre)
        assertEquals("Fernández", entity.apellido)
        assertEquals("73456789", entity.dni)
        assertEquals(41, entity.edad)
        assertTrue(entity.activo)
    }
}
