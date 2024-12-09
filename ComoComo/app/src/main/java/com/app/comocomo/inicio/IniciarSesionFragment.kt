package com.app.comocomo.inicio

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentIniciarSesionBinding


class IniciarSesionFragment : Fragment() {

    private var _binding: FragmentIniciarSesionBinding? = null
    private val binding: FragmentIniciarSesionBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper

  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      // Inflate the layout for this fragment
      _binding = FragmentIniciarSesionBinding.inflate(inflater, container, false)
      val view = binding.root

      db = DatabaseHelper(requireContext())

      binding.nextButton.setOnClickListener {
          val nombre = binding.nombreUsuarioText.text.toString().trim()
          val contraseña = binding.passwordEditText.text.toString().trim()

          if (nombre.isNotEmpty() && contraseña.isNotEmpty()) {
              // Verificar si hay algún usuario ya registrado
              if (db.obtenerUsuariosRegistrados().isEmpty()) {
                  Toast.makeText(requireContext(), "Debes registrarte", Toast.LENGTH_SHORT).show()
              } else {

                  val usuario = db.obtenerUsuariosPorNombre(nombre)

                  if (usuario.isEmpty()) {
                      Toast.makeText(requireContext(), "Usuario no registrado", Toast.LENGTH_SHORT)
                          .show()
                  } else {
                      // Buscar usuarios con el mismo nombre
                      val usuarios = db.obtenerUsuariosPorNombre(nombre)

                      if (usuarios.isEmpty()) {
                          Toast.makeText(
                              requireContext(),
                              "Usuario no registrado",
                              Toast.LENGTH_SHORT
                          ).show()
                      } else {
                          // Verificar la contraseña
                          val usuarioValido = usuarios.firstOrNull { usuario ->
                              verificarContraseña(contraseña, usuario.contraseña)
                          }

                          if (usuarioValido != null) {
                              guardarUsuarioId(usuarioValido.id)
                              findNavController().navigate(R.id.action_iniciarSesionFragment_to_inicioFragment)
                          } else {
                              Toast.makeText(
                                  requireContext(),
                                  "Contraseña incorrecta",
                                  Toast.LENGTH_SHORT
                              ).show()
                          }
                      }
                  }

          }
              } else {
                  Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT)
                      .show()
              }

      }
          return view
  }

    // Función para cifrar la contraseña
    private fun verificarContraseña(contraseña: String, hash: String): Boolean {
        return try{
            org.mindrot.jbcrypt.BCrypt.checkpw(contraseña, hash)
        } catch (e: Exception){
            false
        }
    }

    // sharedpreferences para ser recuperado más tarde sin necesidad de hacer consultas a la bd
    // buscando el id del usuario en el resto de fragmentos
    private fun guardarUsuarioId(id: Int){
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("usuario_id", id)
        editor.apply()
    }
    // Metodo para obtener el ID del usuario desde SharedPreferences en otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}