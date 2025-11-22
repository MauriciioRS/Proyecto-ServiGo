package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.HistorialAdapter
import com.example.proyectofinal11.models.Historial

class TrabajosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trabajos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Encontrar el RecyclerView
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerHistorial)

        // 2. Decirle al RecyclerView cómo organizarse
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 3. Crear datos de prueba
        val datos = listOf(
            Historial("Caso 001", "Miyuki", "10 Nov 2025", "S/50.00", "Finalizado"),
            Historial("Caso 002", "Pedro", "09 Nov 2025", "S/30.00", "En proceso"),
            Historial("Caso 003", "Lucía", "08 Nov 2025", "S/40.00", "Cancelado")
        )

        // 4. Asignar el adapter
        recycler.adapter = HistorialAdapter(datos)
    }
}
