package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.MensajesAdapter
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.MensajeEntity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.content.Intent
import com.example.proyectofinal11.ui.chat.ChatActivity // Importar la nueva Activity

class MensajesFragment : Fragment() {

    private lateinit var adapter: MensajesAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var db: ServiGoDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mensajes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = ServiGoDatabase.getDatabase(requireContext())
        recycler = view.findViewById(R.id.recyclerMensajes)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutMensajes)

        setupRecyclerView()
        setupTabs(tabLayout)

        // Insertar datos de prueba solo una vez
        insertarDatosDePrueba()
    }

    private fun setupRecyclerView() {
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Modificamos la acción del clic que le pasamos al adaptador
        adapter = MensajesAdapter { mensaje ->
            // Acción al hacer clic en un chat: Abrir ChatActivity
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                // Pasamos datos a la nueva Activity para que sepa qué chat mostrar
                putExtra("CHAT_ID", mensaje.id)
                putExtra("CHAT_TITLE", mensaje.titulo)
            }
            startActivity(intent)
        }
        recycler.adapter = adapter
    }

    private fun setupTabs(tabLayout: TabLayout) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val flow = when (tab.position) {
                    1 -> db.mensajeDao().getConversacionesByEstado("Pendiente")
                    2 -> db.mensajeDao().getConversacionesByEstado("Proceso")
                    3 -> db.mensajeDao().getConversacionesByEstado("Terminado")
                    else -> db.mensajeDao().getAllConversaciones() // "Todos"
                }
                observeMensajes(flow)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        tabLayout.getTabAt(0)?.select() // Seleccionar "Todos" por defecto
    }

    private fun observeMensajes(flow: Flow<List<MensajeEntity>>) {
        lifecycleScope.launch {
            flow.collectLatest { listaMensajes ->
                adapter.submitList(listaMensajes)
            }
        }
    }

    private fun insertarDatosDePrueba() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (db.mensajeDao().getAllConversaciones().first().isEmpty()) {
                val listaDePrueba = listOf(
                    // Aquí creamos los mensajes con el título por defecto "Trabajo X"
                    MensajeEntity(id = 1, titulo = "Trabajo 1", nombreProfesional = "Juan Pérez", oficioProfesional = "Gásfiter", ultimoMensaje = "El trabajo ha sido completado. ¿Puede confirmar?", hora = "Ayer", estado = "Terminado", noLeidos = 0),
                    MensajeEntity(id = 2, titulo = "Trabajo 2", nombreProfesional = "María Rodriguez", oficioProfesional = "Electricista", ultimoMensaje = "Ya voy en camino, tuve un pequeño retraso.", hora = "1h", estado = "Proceso", noLeidos = 2),
                    MensajeEntity(id = 3, titulo = "Trabajo 3", nombreProfesional = "Carlos Gómez", oficioProfesional = "Albañil", ultimoMensaje = "Ok, estaré allí en 15 minutos.", hora = "2d", estado = "Pendiente", noLeidos = 0),
                    MensajeEntity(id = 4, titulo = "Trabajo 4", nombreProfesional = "Luis Fernández", oficioProfesional = "Pintor", ultimoMensaje = "¡Hola! Recibí tu solicitud de cotización.", hora = "4d", estado = "Terminado", noLeidos = 0)
                )
                db.mensajeDao().insertAll(listaDePrueba)
            }
        }
    }
}
    