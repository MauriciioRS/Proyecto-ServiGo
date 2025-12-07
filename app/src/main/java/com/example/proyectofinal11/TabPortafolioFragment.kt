package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.PortafolioAdapter
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TabPortafolioFragment : Fragment() {

    private lateinit var db: ServiGoDatabase
    private var usuarioUid: String? = null
    private lateinit var portafolioAdapter: PortafolioAdapter

    // ⭐ 1. AÑADE ESTE BLOQUE "companion object"
    // Esto define la función estática `newInstance` que tu `ProfilePagerAdapter` está buscando.
    companion object {
        private const val ARG_USUARIO_UID = "USUARIO_UID"

        fun newInstance(usuarioUid: String): TabPortafolioFragment {
            val fragment = TabPortafolioFragment()
            val args = Bundle()
            args.putString(ARG_USUARIO_UID, usuarioUid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ServiGoDatabase.getDatabase(requireContext())

        // ⭐ 2. CÓDIGO CORREGIDO PARA LEER EL ARGUMENTO DE FORMA SEGURA
        // Ahora usamos la constante que definimos en el companion object.
        arguments?.let {
            usuarioUid = it.getString(ARG_USUARIO_UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab_recyclerview_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.base_recycler_view)
        val emptyTextView = view.findViewById<TextView>(R.id.empty_text)
        emptyTextView.text = "Este profesional aún no ha subido fotos a su portafolio."

        portafolioAdapter = PortafolioAdapter()
        recyclerView.adapter = portafolioAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // Cuadrícula de 2 columnas

        if (usuarioUid != null) {
            observePortafolio(emptyTextView, recyclerView)
        }
    }

    private fun observePortafolio(emptyView: View, recyclerView: View) {
        lifecycleScope.launch {
            db.portafolioDao().obtenerPortafolioPorUsuario(usuarioUid!!).collectLatest { portafolio ->
                emptyView.isVisible = portafolio.isEmpty()
                recyclerView.isVisible = portafolio.isNotEmpty()
                portafolioAdapter.submitList(portafolio)
            }
        }
    }
}
