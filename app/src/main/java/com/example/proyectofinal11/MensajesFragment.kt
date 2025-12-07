package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.MensajesAdapter
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.MensajeEntity
import com.example.proyectofinal11.vi.chat.ChatActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MensajesFragment : Fragment() {

    private lateinit var adapter: MensajesAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var db: ServiGoDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mensajes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = ServiGoDatabase.getDatabase(requireContext())
        auth = FirebaseAuth.getInstance()
        recycler = view.findViewById(R.id.recyclerMensajes)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutMensajes)

        setupRecyclerView()
        setupTabs(tabLayout)

        insertarDatosDePrueba()
    }

    private fun setupRecyclerView() {
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = MensajesAdapter { mensajeEntity ->
            // Acción al hacer clic en un chat: Abrir ChatActivity
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                // ⭐ Pasamos la información correcta para el chat en tiempo real
                putExtra("CONVERSACION_ID", mensajeEntity.conversacionId)
                // CORRECCIÓN: Usar la clave "RECIPIENT_UID" que espera ChatActivity
                putExtra("RECIPIENT_UID", mensajeEntity.receptorUid)

                // El título combina nombre y oficio (si existe)
                val chatTitle = if (!mensajeEntity.oficioProfesional.isNullOrBlank()) {
                    "${mensajeEntity.nombreProfesional} - ${mensajeEntity.oficioProfesional}"
                } else {
                    mensajeEntity.nombreProfesional
                }
                putExtra("CHAT_TITLE", chatTitle)
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
        val currentUserUid = auth.currentUser?.uid ?: "uid_usuario_actual_default"
        // Estos UIDs deben existir en tu base de datos de usuarios para que el chat funcione
        val profesional1Uid = "uid_profesional_juan"
        val profesional2Uid = "uid_profesional_maria"
        val cliente1Uid = "uid_cliente_carlos"

        fun generarIdDeConversacion(uid1: String, uid2: String): String {
            return if (uid1 < uid2) "$uid1-$uid2" else "$uid2-$uid1"
        }

        lifecycleScope.launch(Dispatchers.IO) {
            if (db.mensajeDao().getAllConversaciones().first().isEmpty()) {
                val listaDePrueba = listOf(
                    MensajeEntity(id = 1, titulo = "Trabajo 1", nombreProfesional = "Juan Pérez", oficioProfesional = "Gásfiter", ultimoMensaje = "El trabajo ha sido completado.", hora = "Ayer", estado = "Terminado", conversacionId = generarIdDeConversacion(currentUserUid, profesional1Uid), receptorUid = profesional1Uid),
                    MensajeEntity(id = 2, titulo = "Trabajo 2", nombreProfesional = "María Rodriguez", oficioProfesional = "Electricista", ultimoMensaje = "Ya voy en camino.", hora = "1h", estado = "Proceso", noLeidos = 2, conversacionId = generarIdDeConversacion(currentUserUid, profesional2Uid), receptorUid = profesional2Uid),
                    MensajeEntity(id = 3, titulo = "Consulta Cliente", nombreProfesional = "Carlos Gómez", oficioProfesional = null, ultimoMensaje = "Hola, necesito una cotización.", hora = "2d", estado = "Pendiente", conversacionId = generarIdDeConversacion(currentUserUid, cliente1Uid), receptorUid = cliente1Uid)
                )
                db.mensajeDao().insertAll(listaDePrueba)
            }
        }
    }
}
