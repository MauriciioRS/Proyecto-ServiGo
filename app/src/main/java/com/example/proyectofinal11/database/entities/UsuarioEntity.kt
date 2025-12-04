package com.example.proyectofinal11.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: String,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val fechaNacimiento: String,
    val direccion: String,
    val distrito: String,
    val email: String,
    val password: String,
    val tipoCuenta: String, // "Cliente", "Contratista", "Ambos"
    val oficio: String? = null,
    val fotoUrl: String? = null
) {
    val nombreCompleto: String
        get() = "$nombres $apellidos"
}




