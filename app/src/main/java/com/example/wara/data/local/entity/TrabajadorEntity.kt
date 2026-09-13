package com.example.wara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trabajadores")
data class TrabajadorEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val edad: Int,
    val activo: Boolean = true,
    val cachedAt: Long = System.currentTimeMillis()
)
