package com.example.wara.domain.usecase.trabajador

import com.example.wara.core.result.Resource
import com.example.wara.domain.model.Trabajador
import com.example.wara.domain.repository.TrabajadorRepository

class CrearTrabajadorUseCase(private val repository: TrabajadorRepository) {
    suspend operator fun invoke(
        nombre: String,
        apellido: String,
        dni: String,
        edad: Int
    ): Resource<Trabajador> {
        val trimmedNombre = nombre.trim()
        val trimmedApellido = apellido.trim()
        val trimmedDni = dni.trim()

        if (trimmedNombre.length < 2 || trimmedNombre.length > 100) {
            return Resource.Error("El nombre debe tener entre 2 y 100 caracteres.")
        }
        if (trimmedApellido.length < 2 || trimmedApellido.length > 100) {
            return Resource.Error("El apellido debe tener entre 2 y 100 caracteres.")
        }
        if (!trimmedDni.matches(Regex("^\\d{8}$"))) {
            return Resource.Error("El DNI debe tener exactamente 8 dígitos numéricos.")
        }
        if (edad < 18 || edad > 80) {
            return Resource.Error("La edad debe estar entre 18 y 80 años.")
        }

        return repository.agregarTrabajador(trimmedNombre, trimmedApellido, trimmedDni, edad)
    }
}
