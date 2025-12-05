package com.example.proyectofinal11.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

// El listener sigue igual, está bien.
data class ProfesionalClickListeners(
    val onFavoritoClick: (UsuarioEntity) -> Unit,
    val onVerPerfilClick: (UsuarioEntity) -> Unit
)

class ProfesionalAdapter(
    private val listeners: ProfesionalClickListeners
) : ListAdapter<UsuarioEntity, ProfesionalAdapter.ProfesionalViewHolder>(ProfesionalDiffCallback()) {

    // ⭐ 1. AÑADIMOS UNA VARIABLE PARA GUARDAR EL UID DEL CLIENTE ACTUAL
    private var clienteActualUid: String? = null

    // ⭐ 2. MÉTODO PÚBLICO PARA QUE EL FRAGMENTO ACTUALICE ESTE UID
    fun setClienteActualUid(uid: String?) {
        this.clienteActualUid = uid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesionalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesional_card, parent, false)
        return ProfesionalViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesionalViewHolder, position: Int) {
        // ⭐ 3. AL DIBUJAR CADA ITEM, PASAMOS EL UID DEL CLIENTE AL VIEWHOLDER
        holder.bind(getItem(position), clienteActualUid, listeners)
    }

    // El ViewHolder ahora recibe el clienteUid para hacer su lógica
    class ProfesionalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imagenFondo: ImageView = view.findViewById(R.id.imagen_fondo)
        private val circleAvatar: ImageView = view.findViewById(R.id.circle_avatar)
        private val nombreProfesional: TextView = view.findViewById(R.id.nombre_profesional)
        private val oficioProfesional: TextView = view.findViewById(R.id.oficio_profesional)
        private val favoritoIcon: ImageView = view.findViewById(R.id.favorito_icon)
        private val btnVerPerfil: ExtendedFloatingActionButton = view.findViewById(R.id.btn_ver_perfil)
        private val db: ServiGoDatabase = ServiGoDatabase.getDatabase(itemView.context)

        // ⭐ 4. EL MÉTODO BIND ES AHORA MUCHO MÁS INTELIGENTE
        fun bind(
            usuario: UsuarioEntity,
            clienteUid: String?,
            listeners: ProfesionalClickListeners
        ) {
            nombreProfesional.text = "${usuario.nombre} ${usuario.apellido}"
            oficioProfesional.text = usuario.oficio

            // Carga de imágenes (Glide) - Sin cambios, está bien
            Glide.with(itemView.context).load(usuario.imagenFondoUrl).into(imagenFondo)
            if (usuario.fotoPerfilBase64 != null) {
                try {
                    val imageBytes = Base64.decode(usuario.fotoPerfilBase64, Base64.DEFAULT)
                    Glide.with(itemView.context).load(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)).circleCrop().into(circleAvatar)
                } catch (e: Exception) { /* No hacer nada si falla */ }
            }

            // Asignación de los Clics
            favoritoIcon.setOnClickListener { listeners.onFavoritoClick(usuario) }
            btnVerPerfil.setOnClickListener { listeners.onVerPerfilClick(usuario) }

            // ⭐ 5. LÓGICA DE FAVORITOS SEGURA Y CORRECTA
            // Solo si hay un cliente logueado, consultamos la tabla 'favoritos'
            if (clienteUid != null) {
                itemView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    val esFavorito = db.favoritoDao().esFavorito(clienteUid, usuario.firebaseUid) > 0
                    actualizarIconoFavorito(esFavorito)
                }
            } else {
                // Si no hay nadie logueado, el corazón siempre está vacío
                actualizarIconoFavorito(false)
            }
        }

        private fun actualizarIconoFavorito(esFavorito: Boolean) {
            val drawableId = if (esFavorito) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            favoritoIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, drawableId))
        }
    }
}
// El DiffCallback no necesita cambios.
class ProfesionalDiffCallback : DiffUtil.ItemCallback<UsuarioEntity>() {
    override fun areItemsTheSame(oldItem: UsuarioEntity, newItem: UsuarioEntity): Boolean {
        return oldItem.firebaseUid == newItem.firebaseUid
    }
    override fun areContentsTheSame(oldItem: UsuarioEntity, newItem: UsuarioEntity): Boolean {
        return oldItem == newItem
    }
}
