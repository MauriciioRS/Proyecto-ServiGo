package com.example.proyectofinal11

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.ResenaAdapter
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.ResenaEntity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TabResenasFragment : Fragment() {

    private lateinit var db: ServiGoDatabase
    private lateinit var auth: FirebaseAuth
    private var profesionalUid: String? = null

    // Vistas
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var emptyReviewsText: TextView
    private lateinit var nuevaResenaRatingBar: RatingBar
    private lateinit var inputReview: EditText
    private lateinit var btnSubirResena: MaterialButton
    private lateinit var resenaAdapter: ResenaAdapter

    // ⭐ INTERFAZ DE COMUNICACIÓN
    interface OnResenaSubidaListener {
        fun onResenaSubida()
    }

    companion object {
        private const val ARG_USUARIO_UID = "USUARIO_UID"

        fun newInstance(usuarioUid: String): TabResenasFragment {
            return TabResenasFragment().apply {
                arguments = Bundle().apply { putString(ARG_USUARIO_UID, usuarioUid) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ServiGoDatabase.getDatabase(requireContext())
        auth = FirebaseAuth.getInstance()
        arguments?.let { profesionalUid = it.getString(ARG_USUARIO_UID) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tab_resenas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (profesionalUid == null) return

        // Inicializar vistas
        reviewsRecyclerView = view.findViewById(R.id.reviews_recycler_view)
        emptyReviewsText = view.findViewById(R.id.empty_reviews_text)
        nuevaResenaRatingBar = view.findViewById(R.id.rating_bar_nueva_resena)
        inputReview = view.findViewById(R.id.inputReview)
        btnSubirResena = view.findViewById(R.id.btnSubirResena)

        setupRecyclerView()
        observeResenas()
        setupBotonSubirResena()
    }

    private fun setupRecyclerView() {
        resenaAdapter = ResenaAdapter()
        reviewsRecyclerView.adapter = resenaAdapter
        reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeResenas() {
        lifecycleScope.launch {
            db.resenaDao().obtenerResenasPorProfesional(profesionalUid!!).collectLatest { resenas ->
                emptyReviewsText.isVisible = resenas.isEmpty()
                reviewsRecyclerView.isVisible = resenas.isNotEmpty()
                resenaAdapter.submitList(resenas)
            }
        }
    }

    private fun setupBotonSubirResena() {
        btnSubirResena.setOnClickListener {
            val clienteUid = auth.currentUser?.uid
            val calificacion = nuevaResenaRatingBar.rating
            val comentario = inputReview.text.toString().trim()

            if (clienteUid == null) {
                Toast.makeText(requireContext(), "Debes iniciar sesión para dejar una reseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (profesionalUid == clienteUid) {
                Toast.makeText(requireContext(), "No puedes dejarte una reseña a ti mismo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (calificacion == 0f) {
                Toast.makeText(requireContext(), "Por favor, selecciona una calificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (comentario.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, escribe un comentario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            subirResena(clienteUid, calificacion, comentario)
        }
    }

    private fun subirResena(clienteUid: String, calificacion: Float, comentario: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Obtener perfil del cliente (para su nombre y foto)
                val cliente = db.usuarioDao().obtenerUsuarioPorFirebaseUid(clienteUid) ?: return@launch

                // 2. Crear la reseña
                val nuevaResena = ResenaEntity(
                    profesionalUid = profesionalUid!!,
                    clienteUid = clienteUid,
                    clienteNombre = "${cliente.nombre} ${cliente.apellido}",
                    clienteFotoUrl = cliente.fotoPerfilBase64,
                    calificacion = calificacion.toDouble(),
                    comentario = comentario,
                    fecha = System.currentTimeMillis()
                )
                db.resenaDao().insertarResena(nuevaResena)

                // 3. ⭐ RECALCULAR Y ACTUALIZAR PROMEDIO ⭐
                val todasLasResenas = db.resenaDao().obtenerListaDeResenasPorProfesional(profesionalUid!!)
                val nuevoRating = if (todasLasResenas.isNotEmpty()) todasLasResenas.map { it.calificacion }.average() else 0.0
                val nuevoNumeroReviews = todasLasResenas.size

                val profesional = db.usuarioDao().obtenerUsuarioPorFirebaseUid(profesionalUid!!)
                profesional?.let {
                    val profesionalActualizado = it.copy(rating = nuevoRating, numeroReviews = nuevoNumeroReviews)
                    db.usuarioDao().actualizarUsuario(profesionalActualizado)
                }

                // 4. Actualizar UI en hilo principal
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Reseña publicada", Toast.LENGTH_SHORT).show()
                    nuevaResenaRatingBar.rating = 0f
                    inputReview.text.clear()
                    // No es necesario recargar aquí, el Flow de `observeResenas` lo hará automáticamente.

                    // 5. ⭐ NOTIFICAR AL PADRE (ProfileFragment) PARA QUE SE ACTUALICE
                    (parentFragment as? OnResenaSubidaListener)?.onResenaSubida()
                }
            } catch (e: Exception) {
                Log.e("TabResenasFragment", "Error al subir reseña", e)
            }
        }
    }
}
