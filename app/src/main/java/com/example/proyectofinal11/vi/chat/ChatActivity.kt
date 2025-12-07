package com.example.proyectofinal11.vi.chat

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.adapters.ChatAdapter
import com.example.proyectofinal11.models.Message
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val mensajesList = mutableListOf<Message>()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private var chatDocId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatTitle = intent.getStringExtra("CHAT_TITLE") ?: "Chat"
        val recipientUid = intent.getStringExtra("RECIPIENT_UID")
        
        // 1. Configurar Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_chat)
        toolbar.title = chatTitle
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // 2. Configurar RecyclerView y Adaptador
        recyclerView = findViewById(R.id.recycler_view_chat)
        chatAdapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // 3. Configurar Firebase Firestore
        val currentUser = auth.currentUser
        if (currentUser != null && recipientUid != null) {
            // Generar un ID único para el chat (uid1_uid2 ordenados alfabéticamente)
            val uids = listOf(currentUser.uid, recipientUid).sorted()
            chatDocId = "${uids[0]}_${uids[1]}"
            escucharMensajes()
        } else {
            // Fallback si faltan datos (opcional: mostrar error)
            Toast.makeText(this, "Error: No se pudo identificar la conversación", Toast.LENGTH_SHORT).show()
        }

        // 4. Configurar envío manual de mensajes
        val inputMessage = findViewById<EditText>(R.id.edit_text_message)
        findViewById<View>(R.id.button_send).setOnClickListener {
            val text = inputMessage.text.toString()
            if (text.isNotBlank()) {
                agregarMensajeAlChat(text)
                inputMessage.text.clear()
            }
        }
    }

    private fun escucharMensajes() {
        chatDocId?.let { id ->
            db.collection("chats").document(id).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Toast.makeText(this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        val nuevosMensajes = snapshots.toObjects(Message::class.java)
                        mensajesList.clear()
                        mensajesList.addAll(nuevosMensajes)
                        
                        chatAdapter.submitList(mensajesList.toList()) {
                            if (mensajesList.isNotEmpty()) {
                                recyclerView.scrollToPosition(mensajesList.size - 1)
                            }
                        }
                    }
                }
        }
    }

    private fun agregarMensajeAlChat(texto: String) {
        val uid = auth.currentUser?.uid ?: return
        
        // Guardar mensaje en Firestore
        if (chatDocId != null) {
            val nuevoMensaje = Message(emisorUid = uid, texto = texto, timestamp = Date())
            db.collection("chats").document(chatDocId!!).collection("messages")
                .add(nuevoMensaje)
                .addOnFailureListener {
                    Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se puede enviar el mensaje", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_work -> {
                mostrarDialogoTrabajo()
                true
            }
            R.id.menu_description -> {
                mostrarDialogoDescripcion()
                true
            }
            R.id.menu_confirm_date -> {
                mostrarCalendario()
                true
            }
            R.id.menu_payment -> {
                mostrarDialogoMonto()
                true
            }
            R.id.menu_project_status -> {
                mostrarOpcionesDeStatus()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Opción 1: Trabajo
    private fun mostrarDialogoTrabajo() {
        val input = EditText(this)
        input.hint = "Ej: Instalación de luminarias"
        
        val container = crearContainerConEditText(input)

        MaterialAlertDialogBuilder(this)
            .setTitle("Definir Trabajo")
            .setView(container)
            .setPositiveButton("Enviar") { _, _ ->
                val texto = input.text.toString()
                if (texto.isNotBlank()) {
                    agregarMensajeAlChat("📌 Trabajo: $texto")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Opción 2: Descripción
    private fun mostrarDialogoDescripcion() {
        val input = EditText(this)
        input.hint = "Ej: Se instalarán 5 lámparas en la sala..."
        
        val container = crearContainerConEditText(input)

        MaterialAlertDialogBuilder(this)
            .setTitle("Añadir Descripción")
            .setView(container)
            .setPositiveButton("Enviar") { _, _ ->
                val texto = input.text.toString()
                if (texto.isNotBlank()) {
                    agregarMensajeAlChat("📝 Descripción: $texto")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Opción 3: Fecha
    private fun mostrarCalendario() {
        try {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Fecha de término")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(supportFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaFormateada = sdf.format(Date(selection))
                agregarMensajeAlChat("📅 Fecha de término: $fechaFormateada")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Opción 4: Pago (Monto -> Método)
    private fun mostrarDialogoMonto() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Ej: 150.00"
        
        val container = crearContainerConEditText(input)

        MaterialAlertDialogBuilder(this)
            .setTitle("Acordar Pago")
            .setMessage("Ingrese el monto (S/):")
            .setView(container)
            .setPositiveButton("Siguiente") { _, _ ->
                val monto = input.text.toString()
                if (monto.isNotEmpty()) {
                    mostrarMetodosDePago(monto)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarMetodosDePago(monto: String) {
        val opciones = arrayOf("Efectivo", "Transferencia Bancaria", "Yape / Plin", "Tarjeta de Crédito")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Método de Pago")
            .setItems(opciones) { _, which ->
                val metodoSeleccionado = opciones[which]
                agregarMensajeAlChat("💰 Pago acordado: S/$monto\n💳 Método: $metodoSeleccionado")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Opción 5: Status
    private fun mostrarOpcionesDeStatus() {
        val estados = arrayOf("Pendiente", "En Proceso", "Terminado", "Cancelado")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Estado del Proyecto")
            .setSingleChoiceItems(estados, -1) { dialog, which ->
                val nuevoEstado = estados[which]
                agregarMensajeAlChat("🔄 Estado Actual: $nuevoEstado")
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun crearContainerConEditText(editText: EditText): LinearLayout {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(50, 10, 50, 0)
        editText.layoutParams = params
        container.addView(editText)
        return container
    }
}
