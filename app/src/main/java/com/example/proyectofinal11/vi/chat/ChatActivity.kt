package com.example.proyectofinal11.ui.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal11.R
import com.google.android.material.appbar.MaterialToolbar

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Obtener los datos pasados desde MensajesFragment
        val chatId = intent.getIntExtra("CHAT_ID", -1)
        val chatTitle = intent.getStringExtra("CHAT_TITLE") ?: "Chat"

        // 2. Configurar la Toolbar (la barra superior)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_chat)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Muestra el botón de atrás
        supportActionBar?.title = chatTitle

        // --- Lógica Futura ---
        // Aquí iría el código para:
        // - Crear un ChatAdapter para el RecyclerView 'recycler_view_chat'.
        // - Consultar la base de datos para obtener los mensajes de este 'chatId'.
        // - Mostrar los mensajes en el RecyclerView.
        Toast.makeText(this, "Abierto chat para: $chatTitle (ID: $chatId)", Toast.LENGTH_LONG).show()
    }

    // 3. Inflar (crear y mostrar) el menú de 3 puntos en la Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    // 4. Manejar los clics en las opciones del menú y el botón de atrás
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Manejar clic en el botón de "Atrás"
                finish() // Cierra esta actividad y regresa a la anterior
                true
            }
            R.id.menu_rename_work -> {
                Toast.makeText(this, "Opción: Cambiar nombre", Toast.LENGTH_SHORT).show()
                // Aquí abrirías un diálogo para que el usuario escriba un nuevo nombre.
                true
            }
            R.id.menu_call -> {
                Toast.makeText(this, "Opción: Llamar", Toast.LENGTH_SHORT).show()
                // Aquí iniciarías la acción para llamar al número del profesional.
                true
            }
            R.id.menu_archive_chat -> {
                Toast.makeText(this, "Opción: Archivar", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
