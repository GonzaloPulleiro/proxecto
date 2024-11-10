package com.app.comocomo

import android.content.Context
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavManager(
    private val navController: NavController,
    private val bottomNav: BottomNavigationView
) {
    init {
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicio -> navigateTo(R.id.inicioFragment)
                R.id.recetas -> navigateTo(R.id.recetaFragment)
                R.id.menu -> navigateTo(R.id.menuFragment)
                R.id.favoritas -> navigateTo(R.id.favoritasFragment)
                R.id.perfil -> navigateTo(R.id.perfilFragment)

            }
            true
        }
    }

    private fun navigateTo(destinationId: Int) {
       navController.navigate(destinationId)
    }
}
