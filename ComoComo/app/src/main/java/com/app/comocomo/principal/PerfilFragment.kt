package com.app.comocomo.principal

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.MainActivity
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding: FragmentPerfilBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var textViewMensaje: TextView
    private lateinit var iconoAdmin: ImageView
    private lateinit var misRecetas: Button
    private lateinit var eliminar: Button
    private lateinit var cerrar: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view = binding.root

        db = DatabaseHelper(requireContext())
        iconoAdmin = _binding!!.iconoAdmin
        misRecetas = _binding!!.recetas
        eliminar = _binding!!.eliminar
        cerrar = _binding!!.cerrar
        textViewMensaje = _binding!!.textViewMensaje

        val usuarioId = obtenerUsuarioId()

        if(usuarioId != -1) {
            val usuario = db.obtenerUsuarioPorId(usuarioId)

            if (usuario != null) {
                // Actualizar nombre usuario en perfil
                    textViewMensaje.text = "Perfil de ${usuario.nombre}"

                // Si es admin, visible icono y botón eliminar usuarios
                if (usuario.esAdmin) {
                    iconoAdmin.visibility = View.VISIBLE
                    eliminar.visibility = View.VISIBLE
                } else {
                    iconoAdmin.visibility = View.GONE
                    eliminar.visibility = View.GONE
                }
            }
        }

        // Cerrar sesión y llamar al metodo creado en Main Activity
        cerrar.setOnClickListener{
            (activity as MainActivity).onUserLogout()
        }
        // Navegar al fragment Mis Recetas
        misRecetas.setOnClickListener{
            findNavController().navigate(R.id.action_perfilFragment_to_misRecetasFragment)
        }
        // Navegar al fragment Lista Usuarios
        eliminar.setOnClickListener{
             findNavController().navigate(R.id.action_perfilFragment_to_listaUsuariosFragment)
        }

        return view
    }

    // Metodo para obtener el ID del usuario desde SharedPreferences desde otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }

}