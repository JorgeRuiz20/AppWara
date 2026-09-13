package com.example.wara.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrabajadorDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("apellido")
    val apellido: String,
    @SerializedName("dni")
    val dni: String,
    @SerializedName("edad")
    val edad: Int
)

data class CrearTrabajadorRequestDto(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("apellido")
    val apellido: String,
    @SerializedName("dni")
    val dni: String,
    @SerializedName("edad")
    val edad: Int
)

data class ActualizarTrabajadorRequestDto(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("apellido")
    val apellido: String,
    @SerializedName("edad")
    val edad: Int
)

data class CrearTrabajadorResponseDto(
    @SerializedName("esExitoso")
    val esExitoso: Boolean,
    @SerializedName("mensaje")
    val mensaje: String,
    @SerializedName("trabajador")
    val trabajador: TrabajadorDto?
)

data class ResultadoPaginadoDto<T>(
    @SerializedName("items")
    val items: List<T> = emptyList(),
    @SerializedName("pagina")
    val pagina: Int = 1,
    @SerializedName("tamanoPagina")
    val tamanoPagina: Int = 10,
    @SerializedName("totalRegistros")
    val totalRegistros: Int = 0,
    @SerializedName("totalPaginas")
    val totalPaginas: Int = 0
)
