package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.HistorialAdapter
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.HistorialEntity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class TrabajosFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var db: ServiGoDatabase
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trabajos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = ServiGoDatabase.getDatabase(requireContext())
        recycler = view.findViewById(R.id.recyclerHistorial)
        tabLayout = view.findViewById(R.id.tabLayout)

        setupRecyclerView()
        setupTabs()
        insertarDatosDePrueba()
    }

    private fun setupRecyclerView() {
        recycler.layoutManager = LinearLayoutManager(requireContext())
        historialAdapter = HistorialAdapter()
        recycler.adapter = historialAdapter
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // CORRECCIÓN: Usamos String para el ID ("1") en lugar de Int (1)
                val idUsuarioActual = "1"

                val flow: Flow<List<HistorialEntity>> = when (tab?.position) {
                    // Pestaña 0: "Solicitudes"
                    0 -> db.historialDao().getMisSolicitudesPorEstado(idUsuarioActual, "En proceso")

                    // Pestaña 1: "Atendidos"
                    1 -> db.historialDao().getMisAtendidosPorEstado(idUsuarioActual, "Finalizado")

                    else -> emptyFlow()
                }
                observeHistorial(flow)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        tabLayout.getTabAt(0)?.select()
    }

    private fun observeHistorial(historialFlow: Flow<List<HistorialEntity>>) {
        lifecycleScope.launch {
            historialFlow.collectLatest { historial ->
                historialAdapter.submitList(historial)
            }
        }
    }

    private fun insertarDatosDePrueba() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (db.historialDao().contarHistorial() == 0) {
                // CORRECCIÓN: Usamos String para los IDs
                val idUsuarioActual = "1"
                val datos = listOf(
                    // --- CASO 1: "SOLICITUD" ---
                    HistorialEntity(
                        clienteId = idUsuarioActual,
                        contratistaId = "2",
                        caso = "Pintar pared de sala",
                        titulo = "Servicio de Pintura",
                        usuario = "Ana la Pintora",
                        fecha = "15 Nov 2025",
                        precio = "S/120.00",
                        estado = "En proceso"
                    ),
                    // --- CASO 2: "ATENDIDO" ---
                    HistorialEntity(
                        clienteId = "3",
                        contratistaId = idUsuarioActual,
                        caso = "Instalar nueva ducha",
                        titulo = "Servicio de Gasfitería",
                        usuario = "Lucía Campos",
                        fecha = "14 Nov 2025",
                        precio = "S/80.00",
                        estado = "Finalizado"
                    ),
                    // --- CASO 3: Otro "ATENDIDO" ---
                    HistorialEntity(
                        clienteId = "4",
                        contratistaId = idUsuarioActual,
                        caso = "Fuga en lavadero",
                        titulo = "Reparación Urgente",
                        usuario = "Pedro Suárez",
                        fecha = "12 Nov 2025",
                        precio = "S/50.00",
                        estado = "En proceso"
                    )
                )
                db.historialDao().insertAll(datos)
            }
        }
    }
}