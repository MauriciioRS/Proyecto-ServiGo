package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal11.adapters.MensajesAdapter
import com.example.proyectofinal11.models.Mensaje
import com.google.android.material.tabs.TabLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

class MensajesFragment : Fragment() {

    private lateinit var adapter: MensajesAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var allMensajes: List<Mensaje>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mensajes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recyclerMensajes)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutMensajes)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Datos de ejemplo (puedes sustituirlos por tu API/BD)
        allMensajes = listOf(
            Mensaje("1","Juan Pérez","Gásfiter","El trabajo ha sido completado. ¿Puede confirmar?","Ayer","Terminado", 0),
            Mensaje("2","María Rodriguez","Electricista","Ya voy en camino, tuve un pequeño retraso.","1h","Proceso", 2),
            Mensaje("3","Carlos Gómez","Albañil","Ok, estaré allí en 15 minutos.","2d","Pendiente", 0),
            Mensaje("4","Luis Fernández","Pintor","¡Hola! Recibí tu solicitud de cotización.","4d","Terminado", 0)
        )

        adapter = MensajesAdapter(allMensajes.toMutableList()) { msg ->
            // Click: abrir chat / detalle
            Toast.makeText(requireContext(), "Abriste: ${msg.nombre}", Toast.LENGTH_SHORT).show()
        }
        recycler.adapter = adapter

        // Tab selection -> filtrar
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val title = tab.text?.toString() ?: "Todos"
                filterByTab(title)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Seleccionar la primera pestaña (Todos) por defecto
        tabLayout.getTabAt(0)?.select()
    }

    private fun filterByTab(tabTitle: String) {
        val filtered = when (tabTitle.lowercase()) {
            getString(R.string.tab_pendiente).lowercase() -> allMensajes.filter { it.estado.equals("Pendiente", true) }
            getString(R.string.tab_proceso).lowercase() -> allMensajes.filter { it.estado.equals("Proceso", true) }
            getString(R.string.tab_terminado).lowercase() -> allMensajes.filter { it.estado.equals("Terminado", true) }
            else -> allMensajes
        }
        adapter.updateList(filtered)
    }
}
