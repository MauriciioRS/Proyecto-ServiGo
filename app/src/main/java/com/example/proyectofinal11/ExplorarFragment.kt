package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.ProfesionalEntity
import com.example.proyectofinal11.adapters.ProfesionalAdapter
import com.example.proyectofinal11.adapters.ProfesionalClickListeners
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ExplorarFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var profesionalAdapter: ProfesionalAdapter
    private lateinit var db: ServiGoDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explorar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = ServiGoDatabase.getDatabase(requireContext())
        setupRecyclerView(view)
        observeProfesionales()
        insertarDatosDePrueba()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_profesionales)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Creamos las funciones que se ejecutarán al hacer clic
        val listeners = ProfesionalClickListeners(
            onFavoritoClick = { profesional ->
                toggleFavorito(profesional)
            },
            onVerPerfilClick = { profesional ->
                verPerfil(profesional)
            }
        )

        // Pasamos las funciones al adaptador
        profesionalAdapter = ProfesionalAdapter(listeners)
        recyclerView.adapter = profesionalAdapter
    }

    private fun observeProfesionales() {
        lifecycleScope.launch {
            db.profesionalDao().getAllProfesionales().collectLatest { listaDeProfesionales ->
                // Con ListAdapter, usamos submitList
                profesionalAdapter.submitList(listaDeProfesionales)
            }
        }
    }

    private fun toggleFavorito(profesional: ProfesionalEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val profesionalActualizado = profesional.copy(esFavorito = !profesional.esFavorito)
            db.profesionalDao().updateProfesional(profesionalActualizado)
        }
    }

    // --- NUEVA FUNCIÓN PARA NAVEGAR AL PERFIL ---
    private fun verPerfil(profesional: ProfesionalEntity) {
        // Usamos el FragmentManager para reemplazar el contenido actual con el PerfilFragment
        parentFragmentManager.commit {
            // Creamos una nueva instancia de PerfilFragment
            val profileFragment = ProfileFragment()

            // (OPCIONAL PERO RECOMENDADO) Pasamos el ID del profesional al fragmento
            val bundle = Bundle().apply {
                putInt("PROFESIONAL_ID", profesional.id)
            }
            profileFragment.arguments = bundle

            add(R.id.main, profileFragment) // Asegúrate que el ID del contenedor sea correcto
            addToBackStack(null) // Permite volver atrás con el botón de retroceso
        }
    }

    private fun insertarDatosDePrueba() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (db.profesionalDao().getAllProfesionales().first().isEmpty()) {
                val listaDePrueba = listOf(
                    // Añadimos URLs de ejemplo para las imágenes
                    ProfesionalEntity(nombre = "Juan Pérez", oficio = "Electricista", rating = 4.8, numeroReviews = 152,
                        imageUrl = "https://images.pexels.com/photos/8092387/pexels-photo-8092387.jpeg",
                        imagenFondoUrl = "https://images.pexels.com/photos/5691535/pexels-photo-5691535.jpeg"),

                    ProfesionalEntity(nombre = "Maria Garcia", oficio = "Plomera", rating = 4.9, numeroReviews = 98,
                        imageUrl = "https://images.pexels.com/photos/8158963/pexels-photo-8158963.jpeg",
                        imagenFondoUrl = "https://images.pexels.com/photos/8463510/pexels-photo-8463510.jpeg",
                        esFavorito = false),

                    ProfesionalEntity(nombre = "Carlos Rodriguez", oficio = "Carpintero", rating = 4.7, numeroReviews = 210,
                        imageUrl = "https://images.pexels.com/photos/3771073/pexels-photo-3771073.jpeg",
                        imagenFondoUrl = "https://images.pexels.com/photos/1249610/pexels-photo-1249610.jpeg")
                )
                db.profesionalDao().insertAll(listaDePrueba)
            }
        }
    }
}
    