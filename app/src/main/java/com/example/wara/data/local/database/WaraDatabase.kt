package com.example.wara.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wara.core.constants.AppConstants
import com.example.wara.data.local.dao.TrabajadorDao
import com.example.wara.data.local.entity.TrabajadorEntity

@Database(
    entities = [TrabajadorEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WaraDatabase : RoomDatabase() {

    abstract fun trabajadorDao(): TrabajadorDao

    companion object {
        @Volatile
        private var INSTANCE: WaraDatabase? = null

        fun getInstance(context: Context): WaraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaraDatabase::class.java,
                    AppConstants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
