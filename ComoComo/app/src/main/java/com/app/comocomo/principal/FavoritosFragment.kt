package com.app.comocomo.principal

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.adapter.FavoritoAdapter
import com.app.comocomo.databinding.FragmentFavoritosBinding
import java.io.IOException

class FavoritosFragment : Fragment() {

    private var _binding: FragmentFavoritosBinding? = null
    private val binding: FragmentFavoritosBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var recetaAdapter: FavoritoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerOrder: Spinner
    private lateinit var spinnerIcon: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var textViewMensaje: TextView
    private var cursor: Cursor? = null
    private var usuarioId: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for requireContext() fragment
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        val view = binding.root

        // Obtener referencias
        db = DatabaseHelper(requireContext())
        recyclerView = _binding!!.recyclerView
        spinnerOrder = _binding!!.spinnerorder
        spinnerIcon= _binding!!.spinnerIcon
        searchEditText = _binding!!.searchEditText
        textViewMensaje = _binding!!.textViewMensaje

        usuarioId = obtenerUsuarioId()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.root.postDelayed({
                cargarFavoritos() // Actualizar los favoritos (u otra acción de carga)
                binding.swipeRefreshLayout.isRefreshing = false // Finalizar el refresh
            }, 1500)
        }


        // Lista de opciones para filtrar u ordenar
        val orders = arrayOf("Por defecto", "Nombre de la receta",
            "Menos tiempo de preparación",
            "Más tiempo de preparación",
            "Ordenar Alfabéticamente")
        // Crear un ArrayAdapter para el Spinner y establecerlo
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, orders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOrder.adapter = adapter
        // Establecer un listener para el Spinner
        spinnerIcon.setOnClickListener{
            spinnerOrder.performClick()
        }
        // Acciones a realizar cuando se selecciona una opción del listado para filtrar u ordenar
        spinnerOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        textViewMensaje.visibility = View.VISIBLE
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        cargarFavoritos()
                    }
                    1 -> {
                        searchEditText.visibility = View.VISIBLE
                        textViewMensaje.visibility = View.GONE
                        searchEditText.postDelayed({
                            if (isAdded && isVisible) {  // Asegurarnos de que el Fragment esté visible y activo
                                searchEditText.requestFocus()  // Asignar foco al EditTex
                                mostrarTeclado()               // Mostrar el teclado
                            }
                        }, 100)
                    }
                    2 -> {
                        textViewMensaje.visibility = View.VISIBLE
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        obtenerRecetasFavoritasOrdenadasPorTiempoPreparacion("ASC")
                    }

                    3 -> {
                        textViewMensaje.visibility = View.VISIBLE
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        obtenerRecetasFavoritasOrdenadasPorTiempoPreparacion("DESC")
                    }
                    4 -> {
                        textViewMensaje.visibility = View.VISIBLE
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        obtenerRecetasFavoritasOrdenadasAlfabeticamente()
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
        // Añadir un TextWatcher para filtrar recetas en tiempo real
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {

                    obtenerRecetasFavoritasPorNombre(query)
                } else {

                    cargarFavoritos()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                textViewMensaje.visibility = View.GONE
            }
        })

        return view
    }

    // Metodo para obtener el ID del usuario desde SharedPreferences desde otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }

    // Cargar las recetas favoritas del usuario
    private fun cargarFavoritos() {
        cursor = db.obtenerRecetasFavoritas(usuarioId)
        updateMessage() // Actualizar el mensaje con el número de recetas favoritas
        if (cursor != null && cursor!!.count > 0) {
            recetaAdapter = FavoritoAdapter(requireContext(), cursor, R.id.favoritosFragment)
            recyclerView.adapter = recetaAdapter
        } else {
            // Si no hay recetas favoritas, no mostrar nada
            recyclerView.visibility = View.GONE
            textViewMensaje.text = "No tienes recetas favoritas"
        }
    }

    // Actualizar número de recetas favoritas
    private fun updateMessage() {
        val totalFavoritas = cursor?.count ?: 0 // Obtener el número de recetas favoritas
        if (totalFavoritas > 0) {
            textViewMensaje.visibility = View.VISIBLE
            textViewMensaje.text = "Tienes un total de $totalFavoritas recetas favoritas"
        } else {
            textViewMensaje.text = "No tienes recetas favoritas"
        }
    }

    fun reloadFavoritos() {
        cursor?.close() // Cerrar el cursor actual
        cargarFavoritos() // Cargar los favoritos
    }

    // Metodo para cargar recetas por tiempo
    private fun obtenerRecetasFavoritasOrdenadasPorTiempoPreparacion(order:String) {
        try{
            db.abrirBaseDeDatos()
            val cursor = db.obtenerRecetasFavoritasOrdenadasPorTiempoPreparacion(order)

            if (cursor != null) {
                if (cursor.count > 0) {
                    val recyclerView = _binding?.recyclerView
                    val layoutManager = LinearLayoutManager(requireContext(),)
                    recyclerView?.layoutManager = layoutManager
                    recetaAdapter = FavoritoAdapter(requireContext(), cursor, R.id.favoritosFragment)
                    recyclerView?.adapter = recetaAdapter
                } else {
                    Toast.makeText(requireContext(), "No hay recetas disponibles", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al abrir la base de datos", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show()
        }

    }

    // Metodo para cargar recetas alfabéticamente
    private fun obtenerRecetasFavoritasOrdenadasAlfabeticamente(){
        try{
            db.abrirBaseDeDatos()
            val cursor = db.obtenerRecetasFavoritasOrdenadasAlfabeticamente()

            if (cursor != null) {
                if (cursor.count > 0) {
                    val recyclerView = _binding?.recyclerView
                    val layoutManager = LinearLayoutManager(requireContext(),)
                    recyclerView?.layoutManager = layoutManager
                    recetaAdapter = FavoritoAdapter(requireContext(), cursor,
                        R.id.favoritosFragment
                    )
                    recyclerView?.adapter = recetaAdapter
                } else {
                    Toast.makeText(requireContext(), "No hay recetas disponibles", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al abrir la base de datos", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show()
        }

    }

    // Metodo para filtrar recetas por nombre en tiempo real
    private fun obtenerRecetasFavoritasPorNombre(nombre: String) {
        val cursor = db.obtenerRecetasFavoritasPorNombre(nombre)
        recetaAdapter.swapCursor(cursor)
    }

    private fun mostrarTeclado() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun ocultarTeclado() {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        // Oculta el teclado y el EditText cuando la actividad vuelve a la vista
        ocultarTeclado()
        searchEditText.visibility = View.GONE
        reloadFavoritos() // Recargar los favoritos al volver a la actividad
    }

    override fun onDestroy() {
        super.onDestroy()
        cursor?.close() // Asegurarte de cerrar el cursor al destruir la actividad
    }

}