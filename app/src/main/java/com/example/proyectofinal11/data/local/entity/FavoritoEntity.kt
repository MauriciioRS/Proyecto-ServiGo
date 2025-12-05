package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val clienteUid: String,         // UID del usuario que marca favorito
    val contratistaUid: String      // UID del profesional marcado
)
