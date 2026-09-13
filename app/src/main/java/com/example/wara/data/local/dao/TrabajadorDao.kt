package com.example.wara.data.local.dao

import androidx.room.*
import com.example.wara.data.local.entity.TrabajadorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrabajadorDao {

    @Query("SELECT * FROM trabajadores WHERE (:dni IS NULL OR dni LIKE '%' || :dni || '%') AND (:soloActivos = 0 OR activo = 1) ORDER BY id DESC")
    suspend fun getTrabajadores(dni: String?, soloActivos: Boolean): List<TrabajadorEntity>

    @Query("SELECT * FROM trabajadores WHERE (:dni IS NULL OR dni LIKE '%' || :dni || '%') AND (:soloActivos = 0 OR activo = 1) ORDER BY id DESC")
    fun observeTrabajadores(dni: String?, soloActivos: Boolean): Flow<List<TrabajadorEntity>>

    @Query("SELECT * FROM trabajadores WHERE id = :id LIMIT 1")
    suspend fun getTrabajadorById(id: Int): TrabajadorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrabajadores(trabajadores: List<TrabajadorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrabajador(trabajador: TrabajadorEntity)

    @Query("UPDATE trabajadores SET activo = 0 WHERE id = :id")
    suspend fun softDeleteTrabajador(id: Int)

    @Query("DELETE FROM trabajadores WHERE id = :id")
    suspend fun deleteTrabajadorById(id: Int)

    @Query("DELETE FROM trabajadores")
    suspend fun clearAll()
}
