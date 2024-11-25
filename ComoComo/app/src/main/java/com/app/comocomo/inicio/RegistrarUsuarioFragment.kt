package com.app.comocomo.inicio

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentRegistrarUsuarioBinding


class RegistrarUsuarioFragment : Fragment() {

    private var _binding: FragmentRegistrarUsuarioBinding? = null
    private val binding: FragmentRegistrarUsuarioBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrarUsuarioBinding.inflate(inflater, container, false)
        val view = binding.root

        db = DatabaseHelper(requireContext())

        binding.nextButton.setOnClickListener {
            val nombre = binding.nombreUsuarioText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val contraseña = binding.passwordEditText.text.toString().trim()

            if (validarDatos(nombre, email, contraseña)) {
                // verificar si nombre ya existe
                val usuarioExistente = db.obtenerUsuarioPorNombreYContraseña(nombre, contraseña)

                if (usuarioExistente != null) {
                    // si nombre existe y contraseña es la misma
                    if (usuarioExistente.contraseña.trim() == contraseña.trim()) {
                        Toast.makeText(
                            requireContext(),
                            "Modifica nombre o contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // si nombre existe pero contraseña es diferente
                        registrarUsuario(nombre, email, contraseña)
                    }
                } else {
                    registrarUsuario(nombre, email, contraseña)
                }
            }
        }
        return view
    }

    private fun registrarUsuario(nombre: String, email: String, contraseña: String){
                // existe un admin?
                if (nombre.equals("admin", ignoreCase = true) && db.existeAdmin()) {
                    Toast.makeText(
                        requireContext(),
                        "No puedes ser administrador",
                        Toast.LENGTH_SHORT
                    ).show()
                    // el email está ya registrado??
                } else if (db.emailYaRegistrado(email)) {
                    Toast.makeText(
                        requireContext(),
                        "Este email ya está registrado",
                        Toast.LENGTH_SHORT
                    ).show()
                    // registrar usuario
                } else {
                    val resultado = db.registrarUsuarioBD(nombre, email, contraseña)
                    if (resultado != -1L) {
                        Toast.makeText(requireContext(), "Registro realizado", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.action_registrarUsuarioFragment_to_iniciarSesionFragment)

                    } else {
                        Toast.makeText(requireContext(), "Error al registarse", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }


    private fun validarDatos(nombre: String, email: String, contraseña: String): Boolean {
        // Si campo nombre está vacío
        if (TextUtils.isEmpty(nombre)) {
            binding.nombreUsuarioInput.error = "Este campo no puede estar vacío"
            return false
        } else {
            binding.nombreUsuarioInput.error = null
        }
        // Si campo email está vacío
        if (TextUtils.isEmpty(email)) {
            binding.emailTextInput.error = "Este campo no puede estar vacío"
            return false
        } else {
            binding.emailTextInput.error = null
        }
        // Comprobar que sea un email válido, con @ .com etc
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailTextInput.error = "Email no válido"
            return false
        } else {
            binding.emailTextInput.error = null
        }
        // Si contraseña está vacia
        if (TextUtils.isEmpty(contraseña)) {
            binding.passwordTextInput.error = "Este campo no puede estar vacío"
            return false
        } else {
            binding.passwordTextInput.error = null
        }

       // Verificar longitud de la contraseña
        if (contraseña.length < 8) {
            binding.passwordTextInput.error = "La contraseña debe tener al menos 8 caracteres"
            return false
        }

        // Verificar que tenga al menos una letra mayúscula, un número y un carácter especial
        val patron = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).*$"
        if (!contraseña.matches(patron.toRegex())) {
            binding.passwordTextInput.error = "Debe incluir al menos una mayúscula, un número y un carácter especial"
            return false
        } else {
            binding.passwordTextInput.error = null
        }


        // Verificar que las contraseñas coincidan
        val confirmaPassword = binding.password2EditText.text.toString().trim()
        if (contraseña != confirmaPassword) {
            binding.password2TextInput.error = "Las contraseñas no coinciden"
            return false
        } else {
            binding.password2TextInput.error = null
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    }