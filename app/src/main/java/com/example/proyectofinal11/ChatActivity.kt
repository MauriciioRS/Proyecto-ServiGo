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
import com.example.proyectofinal11.models.Message
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange // ⭐ MEJORA 1: Importación necesaria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

class ChatActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Vistas y Adaptador
    private lateinit var toolbar: MaterialToolbar
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: FloatingActionButton
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    // Datos del chat
    private lateinit var currentUserUid: String
    private lateinit var conversacionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        currentUserUid = auth.currentUser?.uid ?: return finishWithMessage("Error: Usuario no autenticado.")

        // 2. Recibir datos del Intent
        conversacionId = intent.getStringExtra("CONVERSACION_ID") ?: return finishWithMessage("Error: ID de conversación no encontrado.")
        val chatTitle = intent.getStringExtra("CHAT_TITLE") ?: "Chat"

        Log.d("ChatActivity", "Iniciando chat. ConversacionID: $conversacionId")

        // 3. Configurar UI
        setupViews()
        setupToolbar(chatTitle)
        setupRecyclerView()
        setupSendButton()

        // 4. Escuchar mensajes en tiempo real desde Firestore
        escucharMensajes()
    }

    private fun setupViews() {
        toolbar = findViewById(R.id.toolbar_chat)
        messageEditText = findViewById(R.id.edit_text_message)
        sendButton = findViewById(R.id.button_send)
        chatRecyclerView = findViewById(R.id.recycler_view_chat)
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        chatRecyclerView.adapter = chatAdapter
        // ⭐ MEJORA 2: Se añade 'reverseLayout = true' para un comportamiento de chat natural
        // Ahora `stackFromEnd` y `reverseLayout` trabajan juntos para imitar WhatsApp.
        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }
    }

    private fun setupSendButton() {
        sendButton.setOnClickListener {
            val mensajeTexto = messageEditText.text.toString().trim()
            if (mensajeTexto.isNotEmpty()) {
                enviarMensaje(mensajeTexto)
                messageEditText.text.clear()
            }
        }
    }

    private fun enviarMensaje(texto: String) {
        val nuevoMensaje = Message(
            emisorUid = currentUserUid,
            texto = texto,
            timestamp = Date()
        )

        firestore.collection("chats").document(conversacionId)
            .collection("mensajes").add(nuevoMensaje)
            .addOnSuccessListener {
                Log.d("ChatActivity", "Mensaje enviado con éxito.")
                // No es necesario actualizar la lista manualmente aquí,
                // el 'listener' de `escucharMensajes` lo hará automáticamente.
            }
            .addOnFailureListener { e ->
                Log.e("ChatActivity", "Error al enviar mensaje", e)
                Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun escucharMensajes() {
        // ⭐ MEJORA 3: El `listener` ahora ordena DESCENDENTE para que los nuevos mensajes lleguen primero.
        firestore.collection("chats").document(conversacionId)
            .collection("mensajes").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ChatActivity", "Error al escuchar mensajes", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val mensajes = snapshots.toObjects(Message::class.java)
                    chatAdapter.submitList(mensajes)
                    // No es necesario el scrollToPosition, porque `reverseLayout=true` y el orden descendente
                    // ya se encargan de poner los mensajes nuevos en la parte superior (que es la parte visible inferior).
                }
            }
    }

    private fun finishWithMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    // Métodos para el menú y el botón de atrás, no necesitan cambios.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
