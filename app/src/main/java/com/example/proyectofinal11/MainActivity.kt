package com.example.proyectofinal11

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Pantalla principal de la aplicación ServiGo
 * Contiene la barra de navegación inferior con 5 pestañas
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Ocultar ActionBar completamente
        supportActionBar?.hide()
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)


        loadFragment(InicioFragment())


        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    loadFragment(InicioFragment())
                    true
                }
                R.id.nav_explorar -> {
                    loadFragment(ExplorarFragment())
                    true
                }
                R.id.nav_trabajos -> {
                    loadFragment(TrabajosFragment())
                    true
                }
                R.id.nav_mensajes -> {
                    loadFragment(MensajesFragment())
                    true
                }
                R.id.nav_perfil -> {
                    loadFragment(PerfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }


    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        return false
    }
}
