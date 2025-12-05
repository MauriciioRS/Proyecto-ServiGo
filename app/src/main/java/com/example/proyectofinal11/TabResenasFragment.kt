package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.ResenaAdapter
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.ResenaEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TabResenasFragment : Fragment() {

    private lateinit var db: ServiGoDatabase
    private var profesionalUid: String? = null
    private lateinit var resenaAdapter: ResenaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ServiGoDatabase.getDatabase(requireContext())
        arguments?.let {
            profesionalUid = it.getString("USUARIO_UID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_resenas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.reviews_recycler_view)
        val emptyTextView = view.findViewById<TextView>(R.id.empty_reviews_text)
        val inputReview = view.findViewById<EditText>(R.id.inputReview)
        val btnSubirResena = view.findViewById<Button>(R.id.btnSubirResena)

        resenaAdapter = ResenaAdapter()
        recyclerView.adapter = resenaAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (profesionalUid != null) {
            observeResenas(emptyTextView, recyclerView)
            setupAddReviewButton(inputReview, btnSubirResena)
        }
    }

    private fun observeResenas(emptyView: View, recyclerView: View) {
        lifecycleScope.launch {
            db.resenaDao().obtenerResenasPorProfesional(profesionalUid!!).collectLatest { resenas ->
                emptyView.isVisible = resenas.isEmpty()
                recyclerView.isVisible = resenas.isNotEmpty()
                resenaAdapter.submitList(resenas)
            }
        }
    }

    private fun setupAddReviewButton(input: EditText, button: Button) {
        button.setOnClickListener {
            val comentario = input.text.toString().trim()
            val autor = FirebaseAuth.getInstance().currentUser

            if (comentario.isNotEmpty() && autor != null && profesionalUid != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val nuevaResena = ResenaEntity(
                        profesionalUid = profesionalUid!!,
                        autorNombre = autor.displayName ?: "Usuario Anónimo",
                        autorFotoUrl = autor.photoUrl?.toString(),
                        comentario = comentario,
                        rating = 5f, // Podrías añadir un RatingBar para esto
                        timestamp = System.currentTimeMillis()
                    )
                    db.resenaDao().insertarResena(nuevaResena)
                    withContext(Dispatchers.Main) {
                        input.text.clear()
                    }
                }
            }
        }
    }
}
