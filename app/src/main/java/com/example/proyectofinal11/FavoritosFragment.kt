package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.ProfesionalAdapter
import com.example.proyectofinal11.adapters.ProfesionalClickListeners
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.ProfesionalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var profesionalAdapter: ProfesionalAdapter
    private lateinit var db: ServiGoDatabase
    private lateinit var emptyFavoritosText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = ServiGoDatabase.getDatabase(requireContext())
        emptyFavoritosText = view.findViewById(R.id.empty_favoritos_text)

        setupRecyclerView(view)
        observeFavoritos()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_profesionales)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // --- CORRECCIÓN 1: Crear el adaptador de la manera nueva ---
        // Definimos AMBAS acciones de clic que el adaptador espera.
        val listeners = ProfesionalClickListeners(
            onFavoritoClick = { profesional ->
                toggleFavorito(profesional)
            },
            onVerPerfilClick = { profesional ->
                verPerfil(profesional) // <- También permitimos ver el perfil desde favoritos
            }
        )

        // Pasamos el objeto 'listeners' al constructor.
        profesionalAdapter = ProfesionalAdapter(listeners)
        recyclerView.adapter = profesionalAdapter
    }

    private fun observeFavoritos() {
        lifecycleScope.launch {
            db.profesionalDao().getFavoritos().collectLatest { listaDeFavoritos ->
                // --- CORRECCIÓN 2: Usar submitList() en lugar de actualizarLista() ---
                profesionalAdapter.submitList(listaDeFavoritos)

                emptyFavoritosText.isVisible = listaDeFavoritos.isEmpty()
                recyclerView.isVisible = listaDeFavoritos.isNotEmpty()
            }
        }
    }

    private fun toggleFavorito(profesional: ProfesionalEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val profesionalActualizado = profesional.copy(esFavorito = !profesional.esFavorito)
            db.profesionalDao().updateProfesional(profesionalActualizado)
        }
    }

    // --- NUEVA FUNCIÓN: Lógica para navegar al perfil (igual que en ExplorarFragment) ---
    private fun verPerfil(profesional: ProfesionalEntity) {
        parentFragmentManager.commit {
            val profileFragment = ProfileFragment()
            val bundle = Bundle().apply {
                putInt("PROFESIONAL_ID", profesional.id)
            }
            profileFragment.arguments = bundle
            // Recuerda usar el ID correcto de tu contenedor principal de fragments
            replace(R.id.main, profileFragment)
            addToBackStack(null)
        }
    }
}
