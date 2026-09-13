package com.example.wara.domain.model

data class PaginatedResult<T>(
    val items: List<T>,
    val pagina: Int,
    val tamanoPagina: Int,
    val totalRegistros: Int,
    val totalPaginas: Int
)
