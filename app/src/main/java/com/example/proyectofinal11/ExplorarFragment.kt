package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.ProfesionalAdapter
import com.example.proyectofinal11.database.entities.ProfesionalEntity
import com.example.proyectofinal11.utils.SharedPrefsHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ExplorarFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfesionalAdapter
    private val app: ServiGoApplication by lazy {
        requireActivity().application as ServiGoApplication
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explorar, container, false)
        
        searchView = view.findViewById(R.id.search_view)
        recyclerView = view.findViewById(R.id.recycler_profesionales)
        
        setupRecyclerView()
        loadProfesionales()
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        performSearch(it)
                    } else {
                        loadProfesionales()
                    }
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    loadProfesionales()
                } else {
                    performSearch(newText)
                }
                return false
            }
        })
        
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProfesionalAdapter(
            profesionales = emptyList(),
            onFavoritoClick = { profesional ->
                toggleFavorito(profesional)
            },
            onVerPerfilClick = { profesional ->
                // TODO: Abrir perfil del profesional
                Toast.makeText(requireContext(), "Ver perfil de ${profesional.nombre}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
    }

    private fun loadProfesionales() {
        lifecycleScope.launch {
            app.repository.getAllProfesionales().collectLatest { profesionales ->
                adapter.updateList(profesionales)
            }
        }
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            app.repository.searchProfesionales(query).collectLatest { profesionales ->
                adapter.updateList(profesionales)
            }
        }
    }

    private fun toggleFavorito(profesional: ProfesionalEntity) {
        val userId = SharedPrefsHelper.getCurrentUserId(requireContext())
        if (userId == null) {
            Toast.makeText(requireContext(), "Debes iniciar sesión para agregar favoritos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val isFavorito = app.repository.isFavorito(userId, profesional.id) != null
            if (isFavorito) {
                app.repository.removeFavorito(userId, profesional.id)
                Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
            } else {
                app.repository.addFavorito(userId, profesional.id)
                Toast.makeText(requireContext(), "Agregado a favoritos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}