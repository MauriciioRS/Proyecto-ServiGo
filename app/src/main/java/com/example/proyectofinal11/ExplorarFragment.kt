// CÓDIGO FINAL Y COMPLETO PARA ExplorarFragment.kt
package com.example.proyectofinal11

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.ProfesionalAdapter
import com.example.proyectofinal11.adapters.ProfesionalClickListeners
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.FavoritoEntity
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
// Esta importación es crucial y ya la tenías, ¡bien hecho!
import com.example.proyectofinal11.R

class ExplorarFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var profesionalAdapter: ProfesionalAdapter
    private lateinit var db: ServiGoDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var emptyTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout del fragmento.
        return inflater.inflate(R.layout.fragment_explorar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializa todos los componentes necesarios.
        db = ServiGoDatabase.getDatabase(requireContext())
        auth = FirebaseAuth.getInstance()
        emptyTextView = view.findViewById(R.id.empty_text_explorar)

        // 2. Configura el RecyclerView.
        setupRecyclerView(view)

        // 3. Empieza a observar los datos de la base de datos para actualizar la UI.
        observeProfesionales()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_profesionales)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Define las acciones para los clics en cada tarjeta del profesional.
        val listeners = ProfesionalClickListeners(
            onFavoritoClick = { usuario -> toggleFavorito(usuario) },
            onVerPerfilClick = { usuario -> verPerfil(usuario) }
        )

        // Crea y asigna el adaptador al RecyclerView.
        profesionalAdapter = ProfesionalAdapter(listeners)
        recyclerView.adapter = profesionalAdapter
    }

    private fun observeProfesionales() {
        lifecycleScope.launch {
            // Escucha de forma reactiva los cambios en la tabla de usuarios.
            db.usuarioDao().obtenerTodosLosProfesionales().collectLatest { listaDeProfesionales ->
                Log.d("ExplorarFragment", "Se recibieron ${listaDeProfesionales.size} profesionales de la DB.")

                // Envía la lista (vacía o no) al adaptador para que se actualice.
                profesionalAdapter.submitList(listaDeProfesionales)

                // Muestra u oculta las vistas apropiadas según si la lista está vacía.
                recyclerView.isVisible = listaDeProfesionales.isNotEmpty()
                emptyTextView.isVisible = listaDeProfesionales.isEmpty()
            }
        }
    }

    private fun toggleFavorito(usuario: UsuarioEntity) {
        val contratistaUid = usuario.firebaseUid
        val clienteActualUid = auth.currentUser?.uid

        if (clienteActualUid == null) {
            Log.w("ExplorarFragment", "Intento de marcar favorito sin un usuario logueado.")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = db.favoritoDao()
            val esFavorito = dao.esFavorito(clienteActualUid, contratistaUid) > 0

            if (esFavorito) {
                // ⭐⭐⭐ ¡CORRECCIÓN 1: USAR ARGUMENTOS CON NOMBRE! ⭐⭐⭐
                // Esto asegura que cada valor vaya al parámetro correcto definido en el DAO.
                dao.eliminarFavorito(
                    clienteUid = clienteActualUid,
                    contratistaUid = contratistaUid
                )
            } else {
                // Esta línea ya estaba bien, crea un objeto FavoritoEntity.
                dao.agregarFavorito(FavoritoEntity(clienteUid = clienteActualUid, contratistaUid = contratistaUid))
            }
        }
    }

    private fun verPerfil(usuario: UsuarioEntity) {
        parentFragmentManager.commit {
            // ⭐⭐⭐ ¡CORRECCIÓN 2: USAR EL ID DEL CONTENEDOR CORRECTO! ⭐⭐⭐
            // Se reemplaza R.id.profileFragment por R.id.main, que es el contenedor.
            replace(R.id.main, ProfileFragment.newInstance(usuario.firebaseUid))
            addToBackStack(null) // Permite al usuario regresar con el botón de atrás.
        }
    }
}
