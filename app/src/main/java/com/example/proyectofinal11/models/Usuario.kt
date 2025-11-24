package com.example.proyectofinal11.models


data class Usuario(
        val nombre: String,
        val oficio: String,
        val email: String,
        val telefono: String,
        val ubicacion: String,
        val experiencia: String,
        val rating: Double,
        val reviews: Int,
        val avatarUrl: String? = null
)
