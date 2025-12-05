package com.example.proyectofinal11

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.ChatAdapter
import com.example.proyectofinal11.data.local.entity.MensajeChatEntity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Vistas y Adaptador
    private lateinit var toolbar: MaterialToolbar
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: FloatingActionButton
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    // Datos del chat
    private var receptorUid: String? = null
    private var receptorNombre: String? = null
    private var conversacionId: String? = null

    // Lista local de mensajes
    private val listaDeMensajes = mutableListOf<MensajeChatEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Inicializar
        auth = FirebaseAuth.getInstance()
        toolbar = findViewById(R.id.toolbar_chat)
        messageEditText = findViewById(R.id.edit_text_message)
        sendButton = findViewById(R.id.button_send)
        chatRecyclerView = findViewById(R.id.recycler_view_chat)

        // 2. Recibir datos
        receptorUid = intent.getStringExtra("RECEPTOR_UID")
        receptorNombre = intent.getStringExtra("RECEPTOR_NOMBRE")

        // 3. Configurar UI
        setupToolbar()
        setupRecyclerView()
        setupSendButton()

        // 4. Validar y cargar datos iniciales
        if (receptorUid == null) {
            Toast.makeText(this, "Error: No se pudo identificar al destinatario.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Generamos un ID único para esta conversación
        conversacionId = generarIdDeConversacion(auth.currentUser!!.uid, receptorUid!!)
        Log.d("ChatActivity", "ID de conversación: $conversacionId")

        // Aquí cargarías los mensajes guardados de la base de datos
        // cargarMensajesAnteriores(conversacionId!!)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = receptorNombre ?: "Chat"
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        chatRecyclerView.adapter = chatAdapter
        val layoutManager = LinearLayoutManager(this)
        // Para que la lista empiece mostrando los últimos mensajes
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager
    }

    private fun setupSendButton() {
        sendButton.setOnClickListener {
            val mensajeTexto = messageEditText.text.toString().trim()
            if (mensajeTexto.isNotEmpty()) {
                enviarMensaje(mensajeTexto)
                messageEditText.text.clear() // Limpiamos el campo de texto
            }
        }
    }

    private fun enviarMensaje(texto: String) {
        val emisorUid = auth.currentUser?.uid
        if (emisorUid == null || receptorUid == null || conversacionId == null) {
            Toast.makeText(this, "Error: No se puede enviar el mensaje.", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Crear el objeto del nuevo mensaje
        val nuevoMensaje = MensajeChatEntity(
            conversacionId = conversacionId!!,
            emisorUid = emisorUid,
            receptorUid = receptorUid!!,
            texto = texto,
            timestamp = System.currentTimeMillis()
        )

        // 2. Añadir el nuevo mensaje a nuestra lista local
        listaDeMensajes.add(nuevoMensaje)

        // 3. Notificar al adaptador para que muestre el nuevo mensaje en la pantalla
        // Usamos toList() para crear una copia nueva de la lista, que es lo que ListAdapter necesita.
        chatAdapter.submitList(listaDeMensajes.toList())

        // 4. Mover la vista al último mensaje
        chatRecyclerView.scrollToPosition(listaDeMensajes.size - 1)

        // (Opcional pero recomendado) Guardar el mensaje en la base de datos en segundo plano
        // lifecycleScope.launch { db.mensajeDao().insertarMensaje(nuevoMensaje) }
    }

    // Genera un ID de chat consistente entre dos usuarios, sin importar quién inicia.
    private fun generarIdDeConversacion(uid1: String, uid2: String): String {
        return if (uid1 < uid2) {
            "$uid1-$uid2"
        } else {
            "$uid2-$uid1"
        }
    }

    // El resto de tu código para el menú y el botón de atrás está bien
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_rename_work -> {
                Toast.makeText(this, "Cambiar nombre presionado", Toast.LENGTH_SHORT).show()
                true
            }
            // etc...
            else -> super.onOptionsItemSelected(item)
        }
    }
}
