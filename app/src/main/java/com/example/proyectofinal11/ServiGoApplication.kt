package com.example.proyectofinal11

import android.app.Application
import com.example.proyectofinal11.database.Repository
import com.example.proyectofinal11.database.entities.ProfesionalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiGoApplication : Application() {
    
    val repository: Repository by lazy {
        Repository(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        initializeProfesionales()
    }
    
    private fun initializeProfesionales() {
        CoroutineScope(Dispatchers.IO).launch {
            // Insertar profesionales de ejemplo
            // OnConflictStrategy.REPLACE evitará duplicados si ya existen
            insertProfesionalesEjemplo()
        }
    }
    
    private suspend fun insertProfesionalesEjemplo() {
                // Insertar profesionales de ejemplo
                val profesionalesEjemplo = listOf(
                    ProfesionalEntity(
                        id = "prof_001",
                        nombre = "Juan Pérez",
                        oficio = "Gasfitería",
                        calificacion = 4.5f,
                        precioHora = 35.00,
                        ubicacion = "Av. Principal 123",
                        distrito = "San Isidro",
                        descripcion = "Especialista en plomería y gasfitería con más de 10 años de experiencia",
                        trabajosCompletados = 150
                    ),
                    ProfesionalEntity(
                        id = "prof_002",
                        nombre = "María Rodriguez",
                        oficio = "Electricidad",
                        calificacion = 4.8f,
                        precioHora = 40.00,
                        ubicacion = "Jr. Los Olivos 456",
                        distrito = "Miraflores",
                        descripcion = "Electricista certificada, instalaciones residenciales e industriales",
                        trabajosCompletados = 200
                    ),
                    ProfesionalEntity(
                        id = "prof_003",
                        nombre = "Carlos Gómez",
                        oficio = "Albañilería",
                        calificacion = 4.2f,
                        precioHora = 30.00,
                        ubicacion = "Calle Real 789",
                        distrito = "Lima",
                        descripcion = "Construcción y reparaciones en general",
                        trabajosCompletados = 120
                    ),
                    ProfesionalEntity(
                        id = "prof_004",
                        nombre = "Luis Fernández",
                        oficio = "Pintura",
                        calificacion = 4.7f,
                        precioHora = 25.00,
                        ubicacion = "Av. Brasil 321",
                        distrito = "San Borja",
                        descripcion = "Pintor profesional, interiores y exteriores",
                        trabajosCompletados = 180
                    ),
                    ProfesionalEntity(
                        id = "prof_005",
                        nombre = "Ana Martínez",
                        oficio = "Jardinería",
                        calificacion = 4.6f,
                        precioHora = 20.00,
                        ubicacion = "Jr. Las Flores 654",
                        distrito = "Surco",
                        descripcion = "Diseño y mantenimiento de jardines",
                        trabajosCompletados = 95
                    ),
                    ProfesionalEntity(
                        id = "prof_006",
                        nombre = "Roberto Silva",
                        oficio = "Carpintería",
                        calificacion = 4.4f,
                        precioHora = 45.00,
                        ubicacion = "Av. Javier Prado 987",
                        distrito = "La Molina",
                        descripcion = "Muebles a medida y reparaciones",
                        trabajosCompletados = 110
                    ),
                    ProfesionalEntity(
                        id = "prof_007",
                        nombre = "Carmen López",
                        oficio = "Limpieza",
                        calificacion = 4.9f,
                        precioHora = 15.00,
                        ubicacion = "Calle Las Begonias 147",
                        distrito = "San Isidro",
                        descripcion = "Limpieza profunda de hogares y oficinas",
                        trabajosCompletados = 250
                    ),
                    ProfesionalEntity(
                        id = "prof_008",
                        nombre = "Pedro Vargas",
                        oficio = "Mecánica",
                        calificacion = 4.3f,
                        precioHora = 50.00,
                        ubicacion = "Av. Arequipa 258",
                        distrito = "Miraflores",
                        descripcion = "Reparación de vehículos y motos",
                        trabajosCompletados = 175
                    )
                )
                
            repository.insertProfesionales(profesionalesEjemplo)
        }
    }
