package com.app.comocomo.perfil

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.adapter.RecetaEliminarAdapter
import com.app.comocomo.databinding.FragmentEliminarRecetaBinding


class EliminarRecetaFragment : Fragment() {
    private var _binding: FragmentEliminarRecetaBinding? = null
    private val binding: FragmentEliminarRecetaBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var recetaAdapter: RecetaEliminarAdapter
    private var usuarioId: Int = 0
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEliminarRecetaBinding.inflate(inflater, container, false)
        val view = binding.root

        db = DatabaseHelper(requireContext())

        toolbar = _binding!!.toolbar

        // Configurar la Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // Vuelve al fragmento anterior
        }

        recyclerView = binding.recyclerViewEliminarReceta

        usuarioId = obtenerUsuarioId()

        cargarRecetas()

        return view
    }

    // Metodo para obtener el ID del usuario desde SharedPreferences desde otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }

    private fun cargarRecetas() {
        try {
            db.abrirBaseDeDatos()
            val cursor = db.obtenerRecetasPorUsuario(usuarioId)
            // Verificar si el cursor tiene al menos un elemento
            if (cursor.count > 0) {
                val layoutManager = LinearLayoutManager(requireContext())
                recyclerView.layoutManager = layoutManager

                recetaAdapter = RecetaEliminarAdapter(requireContext(), cursor) { id ->
                    eliminarReceta(id)  // Cuando se hace clic en eliminar, eliminamos la receta
                }

                recyclerView.adapter = recetaAdapter
            } else {
                Toast.makeText(requireContext(), "No tienes recetas subidas", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al cargar las recetas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarReceta(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Receta")
        builder.setMessage("¿Estás seguro de que quieres eliminar esta receta?")

        builder.setPositiveButton("Sí") { _, _ ->
            val success = db.eliminarReceta(id)
            if (success) {
                Toast.makeText(requireContext(), "Receta eliminada", Toast.LENGTH_SHORT).show()
                cargarRecetas()  // Actualizar el listado después de eliminar la receta
            } else {
                Toast.makeText(requireContext(), "Error al eliminar la receta", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("No", null)
        builder.show()
    }
}
