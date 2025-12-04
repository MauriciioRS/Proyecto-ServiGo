package com.example.proyectofinal11.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favoritos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProfesionalEntity::class,
            parentColumns = ["id"],
            childColumns = ["profesionalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["usuarioId", "profesionalId"]
)
data class FavoritoEntity(
    val usuarioId: String,
    val profesionalId: String,
    val fechaAgregado: Long = System.currentTimeMillis()
)




