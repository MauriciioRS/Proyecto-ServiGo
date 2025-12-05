package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.entity.MensajeChatEntity
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter : ListAdapter<MensajeChatEntity, ChatAdapter.MensajeViewHolder>(MensajeDiffCallback()) {

    // Definimos constantes para los tipos de vista.
    private val TIPO_VISTA_ENVIADO = 1
    private val TIPO_VISTA_RECIBIDO = 2
    private val uidUsuarioActual = FirebaseAuth.getInstance().currentUser?.uid

    // Esta función es la clave: decide qué layout usar.
    override fun getItemViewType(position: Int): Int {
        val mensaje = getItem(position)
        return if (mensaje.emisorUid == uidUsuarioActual) {
            TIPO_VISTA_ENVIADO
        } else {
            TIPO_VISTA_RECIBIDO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = if (viewType == TIPO_VISTA_ENVIADO) {
            layoutInflater.inflate(R.layout.item_chat_message_sent, parent, false)
        } else { // TIPO_VISTA_RECIBIDO
            layoutInflater.inflate(R.layout.item_chat_message_received, parent, false)
        }
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = getItem(position)
        holder.bind(mensaje)
    }

    class MensajeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mensajeTextView: TextView = view.findViewById(R.id.text_view_message)

        fun bind(mensaje: MensajeChatEntity) {
            mensajeTextView.text = mensaje.texto
        }
    }

    class MensajeDiffCallback : DiffUtil.ItemCallback<MensajeChatEntity>() {
        override fun areItemsTheSame(oldItem: MensajeChatEntity, newItem: MensajeChatEntity): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: MensajeChatEntity, newItem: MensajeChatEntity): Boolean {
            return oldItem == newItem
        }
    }
}
