package com.app.comocomo.perfil

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentMisRecetasBinding

class MisRecetasFragment : Fragment() {

    private var _binding: FragmentMisRecetasBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var textViewRecetas: TextView
    private lateinit var subirReceta: Button
    private lateinit var editarReceta: Button
    private lateinit var eliminarReceta: Button
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMisRecetasBinding.inflate(inflater, container, false)
        val view = binding.root


        db = DatabaseHelper(requireContext())

        textViewRecetas = binding.textViewMensaje
        subirReceta = binding.subirReceta
        editarReceta = binding.editarReceta
        eliminarReceta = binding.eliminarReceta


        toolbar = binding.toolbar

        // Configurar la Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // Vuelve al fragmento anterior
        }

        // Obtener el número de recetas del usuario
        val usuarioId = obtenerUsuarioId()
        if (usuarioId == -1) {
            textViewRecetas.text = "Error: No se pudo obtener el usuario logeado."
            return view
        }
        val cantidadRecetas = db.obtenerTotalRecetasUsuario(usuarioId)
        if (cantidadRecetas == 0) {
            textViewRecetas.text = "Aún no has subido recetas. ¡Anímate a compartir tus recetas!"
        } else {
            textViewRecetas.text = "Has aportado un total de $cantidadRecetas recetas"
        }

        // Configurar la navegación de los botones
        subirReceta.setOnClickListener {
            findNavController().navigate(R.id.action_misRecetasFragment_to_subirRecetaFragment)
        }

        editarReceta.setOnClickListener {
            findNavController().navigate(R.id.action_misRecetasFragment_to_editarRecetaFragment)
        }

        eliminarReceta.setOnClickListener {
            findNavController().navigate(R.id.action_misRecetasFragment_to_eliminarRecetaFragment)
        }

        return view
    }

    // Metodo para obtener el ID del usuario desde SharedPreferences desde otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }

}

