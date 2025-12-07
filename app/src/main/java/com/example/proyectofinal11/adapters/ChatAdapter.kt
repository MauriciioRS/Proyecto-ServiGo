package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.models.Message // ⭐ Usa el modelo de Firestore
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private val TIPO_VISTA_ENVIADO = 1
    private val TIPO_VISTA_RECIBIDO = 2
    private val uidUsuarioActual = FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        val mensaje = getItem(position)
        return if (mensaje.emisorUid == uidUsuarioActual) {
            TIPO_VISTA_ENVIADO
        } else {
            TIPO_VISTA_RECIBIDO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = if (viewType == TIPO_VISTA_ENVIADO) {
            inflater.inflate(R.layout.item_chat_message_sent, parent, false)
        } else {
            inflater.inflate(R.layout.item_chat_message_received, parent, false)
        }
        // Devuelve el ViewHolder correcto según el tipo de vista
        return if (viewType == TIPO_VISTA_ENVIADO) SentMessageViewHolder(view) else ReceivedMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        // Llama al `bind` del ViewHolder específico
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Asegúrate de que el ID en `item_chat_message_sent.xml` sea `text_view_message`
        private val mensajeTextView: TextView = view.findViewById(R.id.text_view_message)
        fun bind(mensaje: Message) {
            mensajeTextView.text = mensaje.texto
        }
    }

    class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Asegúrate de que el ID en `item_chat_message_received.xml` sea `text_view_message`
        private val mensajeTextView: TextView = view.findViewById(R.id.text_view_message)
        fun bind(mensaje: Message) {
            mensajeTextView.text = mensaje.texto
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.timestamp == newItem.timestamp // Usamos timestamp como ID único
        }
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
