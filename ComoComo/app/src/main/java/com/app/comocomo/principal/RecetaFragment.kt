package com.app.comocomo.principal

import android.content.Context
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.adapter.RecetaAdapter
import com.app.comocomo.databinding.FragmentRecetaBinding
import java.io.IOException

class RecetaFragment : Fragment() {

    private var _binding: FragmentRecetaBinding? = null
    private val binding: FragmentRecetaBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var recetaAdapter: RecetaAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerOrder: Spinner
    private lateinit var spinnerIcon: ImageView
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecetaBinding.inflate(inflater, container, false)
        val view = binding.root

        // Obtener referencias
        db = DatabaseHelper(requireContext())
        recyclerView = _binding!!.recyclerView
        spinnerOrder = _binding!!.spinnerorder
        spinnerIcon= _binding!!.spinnerIcon
        searchEditText = _binding!!.searchEditText


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.root.postDelayed({ //Hacer que dure más el refresh
                cargarRecetasporId() // Actualizar las recetas (u otra acción de carga)
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
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        cargarRecetasporId()
                    }
                    1 -> {
                        searchEditText.visibility = View.VISIBLE
                        searchEditText.postDelayed({
                            if (isAdded && isVisible) {  // Asegurarnos de que el Fragment esté visible y activo
                                searchEditText.requestFocus()  // Asignar foco al EditTex
                                mostrarTeclado()
                            }
                        }, 100)
                    }
                    2 -> {
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        cargarRecetasOrdenadasPorTiempo("ASC")
                    }

                    3 -> {
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        cargarRecetasOrdenadasPorTiempo("DESC")
                    }
                    4 -> {
                        searchEditText.visibility = View.GONE
                        ocultarTeclado()
                        cargarRecetasOrdenadasAlfabeticamente()
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
                val seleccion = s.toString().trim()
                if (seleccion.isNotEmpty()) {
                    buscarRecetasPorNombre(seleccion)
                } else {
                    cargarRecetasporId()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    // Metodo para cargar recetas
    private fun cargarRecetasporId() {
        try {
            db.abrirBaseDeDatos()
            val cursor = db.obtenerRecetas()

            if (cursor.count > 0) {
                val recyclerView = _binding?.recyclerView
                val layoutManager = GridLayoutManager(requireContext(), 2)
                recyclerView?.layoutManager = layoutManager
                recetaAdapter = RecetaAdapter(requireContext(), cursor, R.id.recetaFragment)
                recyclerView?.adapter = recetaAdapter
            } else {
                Toast.makeText(requireContext(), "No hay recetas disponibles", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al abrir la base de datos", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show()
        }
    }

    // Metodo para cargar recetas por tiempo
    private fun cargarRecetasOrdenadasPorTiempo(order:String) {
        try{
            db.abrirBaseDeDatos()
            val cursor = db.obtenerRecetasOrdenadasPorTiempoPreparacion(order)

            if (cursor.count > 0) {
                val recyclerView = _binding?.recyclerView
                val layoutManager = GridLayoutManager(requireContext(), 2)
                recyclerView?.layoutManager = layoutManager
                recetaAdapter = RecetaAdapter(requireContext(), cursor, R.id.recetaFragment)
                recyclerView?.adapter = recetaAdapter
            } else {
                Toast.makeText(requireContext(), "No hay recetas disponibles", Toast.LENGTH_SHORT).show()
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
    private fun cargarRecetasOrdenadasAlfabeticamente(){
        try{
            db.abrirBaseDeDatos()
            val cursor = db.obtenerRecetasOrdenadasAlfabeticamente()

            if (cursor.count > 0) {
                val recyclerView = _binding?.recyclerView
                val layoutManager = GridLayoutManager(requireContext(), 2)
                recyclerView?.layoutManager = layoutManager
                recetaAdapter = RecetaAdapter(requireContext(), cursor, R.id.recetaFragment)
                recyclerView?.adapter = recetaAdapter
            } else {
                Toast.makeText(requireContext(), "No hay recetas disponibles", Toast.LENGTH_SHORT).show()
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
    private fun buscarRecetasPorNombre(nombre: String) {
        val cursor = db.obtenerRecetaPorNombre(nombre)
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

    override fun onPause() {
        super.onPause()
        ocultarTeclado()
    }

    override fun onResume() {
        super.onResume()
        // Oculta el teclado y el EditText cuando la actividad vuelve a la vista
        ocultarTeclado()
        searchEditText.visibility = View.GONE

    }

}
