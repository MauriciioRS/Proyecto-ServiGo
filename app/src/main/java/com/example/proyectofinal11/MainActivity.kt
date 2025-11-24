package com.example.proyectofinal11

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        supportActionBar?.hide()
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        loadFragment(ExplorarFragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explorar -> {
                    loadFragment(ExplorarFragment())
                    true
                }
                R.id.nav_favoritos -> {
                    loadFragment(FavoritosFragment())
                    true
                }
                R.id.nav_trabajo -> {
                    loadFragment(TrabajosFragment())
                    true
                }
                R.id.nav_mensaje -> {
                    loadFragment(MensajesFragment())
                    true
                }
                R.id.nav_perfil -> {
                    loadFragment(PerfiLAdapter())
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
    
    override fun onPrepareOptionsMenu(menu: android.view.Menu?): Boolean {
        return false
    }
}

