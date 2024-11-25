package com.app.comocomo.perfil

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.adapter.EditarRecetaAdapter
import com.app.comocomo.databinding.FragmentEditarRecetaBinding


class EditarRecetaFragment : Fragment() {
    private var _binding: FragmentEditarRecetaBinding? = null
    private val binding: FragmentEditarRecetaBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var recetaAdapter: EditarRecetaAdapter
    private var usuarioId: Int = 0
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditarRecetaBinding.inflate(inflater, container, false)
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

        recyclerView = binding.recyclerViewEditarRecetas

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

                // Crear el adaptador y asignarlo al RecyclerView
                recetaAdapter = EditarRecetaAdapter(requireContext(), cursor) { recetaId, ->
                    editarReceta(recetaId)  // Cuando se hace clic en eliminar, eliminamos la receta
                }
                // Asignar el adaptador al RecyclerView
                recyclerView.adapter = recetaAdapter
            } else {
                Toast.makeText(requireContext(), "No tienes recetas subidas", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al cargar las recetas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editarReceta(id: Int) {
        // Navegar al fragmento de edición de receta
        val bundle = Bundle().apply {
            // Pasa el ID de la receta
            putInt("recetaId", id)
        }
        findNavController().navigate(R.id.action_editarRecetaFragment_to_editarRecetaDetalleFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        // Aquí se puede refrescar el cursor en caso de que se hayan realizado cambios en la base de datos
        val recetaCursor = db.obtenerRecetas()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Liberar los recursos del binding
        _binding = null
    }
}





