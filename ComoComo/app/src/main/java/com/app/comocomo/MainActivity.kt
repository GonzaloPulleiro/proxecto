package com.app.comocomo

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var db : DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Iniciar base de datos
        db = DatabaseHelper(this)
        try{
            db.abrirBaseDeDatos()
            Log.d("MainActivity", "Base de datos creada con exito")
        } catch (e: IOException){
            Log.e("MainActivity", "Error al abrir la base de datos", e)
        }

        // Acceder a la barra de navegación
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Para navegar con la barra
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController

        // Sincronizar bottomnavigation con navcontroller para navegación desde bottomnavigation
        bottomNavigationView.setupWithNavController(navController)

        // Para que al cerrar sesión y volver a iniciar, no aparezca seleccionado item perfil
        bottomNavigationView.selectedItemId = R.id.inicio

        // Lógica para que el bottom navigation no se muestre en los 3 fragmentos de inicio
        // y también para que se oculte cuando se abre el teclado del dispositivo
        val vistaRoot = findViewById<View>(android.R.id.content)
        vistaRoot.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            vistaRoot.getWindowVisibleDisplayFrame(rect)
            val screenHeight = vistaRoot.rootView.height
            val keypadHeight = screenHeight - rect.height()

            val currentDestinationId = navController.currentDestination?.id

            if(keypadHeight > screenHeight * 0.15 ||
                currentDestinationId == R.id.iniciarSesionFragment ||
                currentDestinationId == R.id.registrarUsuarioFragment ||
                currentDestinationId== R.id.inicialFragment
                ){
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

        // Cambiar iconos del bottom navigation según seleccion y navegar al fragment correspondiente
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicio -> {
                    navigateToFragment(R.id.inicioFragment)
                    true
                }
                R.id.receta -> {
                    navigateToFragment(R.id.recetaFragment)
                    true
                }
                R.id.menu -> {
                    navigateToFragment(R.id.menuFragment)
                    true
                }
                R.id.favoritos -> {
                    navigateToFragment(R.id.favoritosFragment)
                    true
                }
                R.id.perfil -> {
                    navigateToFragment(R.id.perfilFragment)
                    true
                }
                else -> false
            }
        }

    }

    // Gestionar la navegación y actualizar los iconos del BottomNavigationView
    private fun navigateToFragment(fragmentId: Int) {
        when (fragmentId) {
            R.id.inicioFragment -> {
                navController.navigate(R.id.inicioFragment)
                actualizaIconosBottom(R.id.inicio)
            }
            R.id.recetaFragment -> {
                navController.navigate(R.id.recetaFragment)
                actualizaIconosBottom(R.id.receta)
            }
            R.id.menuFragment -> {
                navController.navigate(R.id.menuFragment)
                actualizaIconosBottom(R.id.menu)
            }
            R.id.favoritosFragment -> {
                navController.navigate(R.id.favoritosFragment)
                actualizaIconosBottom(R.id.favoritos)
            }
            R.id.perfilFragment -> {
                navController.navigate(R.id.perfilFragment)
                actualizaIconosBottom(R.id.perfil)
            }
        }
    }

    // Actualizar los iconos del BottomNavigationView
    private fun actualizaIconosBottom(selectedId: Int) {
        // Iconos de base
        changeIcon(R.id.inicio, R.drawable.ic_home)
        changeIcon(R.id.receta, R.drawable.ic_recipe)
        changeIcon(R.id.menu, R.drawable.ic_menu)
        changeIcon(R.id.favoritos, R.drawable.ic_favorite)
        changeIcon(R.id.perfil, R.drawable.ic_user)

        // Actualiza el ícono seleccionado
        when (selectedId) {
            // Iconos una vez seleccionado el fragment
            R.id.inicio -> changeIcon(R.id.inicio, R.drawable.ic_home2)
            R.id.receta -> changeIcon(R.id.receta, R.drawable.ic_recipe2)
            R.id.menu -> changeIcon(R.id.menu, R.drawable.ic_menu2)
            R.id.favoritos -> changeIcon(R.id.favoritos, R.drawable.ic_favorite2)
            R.id.perfil -> changeIcon(R.id.perfil, R.drawable.ic_user2)
        }
    }

    // Cambiar el icono de un item seleccionado
    private fun changeIcon(itemId: Int, drawableId: Int) {
        val menuItem = bottomNavigationView.menu.findItem(itemId)
        menuItem.icon = ContextCompat.getDrawable(this, drawableId)
    }

    // Cierre de sesión
    fun onUserLogout() {
        val sharedPreferences = getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("usuario_id")
        editor.apply()
        // Forzamos a que el BottomNavigationView seleccione el ítem "Inicio"
        bottomNavigationView.selectedItemId = R.id.inicio

        // Redirigir al fragmento de inicio
        navController.navigate(R.id.inicialFragment)
    }

}




