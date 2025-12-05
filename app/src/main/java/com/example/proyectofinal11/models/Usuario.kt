package com.example.proyectofinal11.models

data class Usuario(
        val nombre: String = "",
        val oficio: String = "",
        val email: String = "",
        val telefono: String = "",
        val ubicacion: String = "",
        val experiencia: String = "",
        val reviews: Int = 0,
        val rating: Double = 0.0,
        val esFavorito: Boolean = false,

        // ⬅️ CAMPO QUE NECESITA TU PerfilFragment
        val urlFotoPerfil: String? = null
)
