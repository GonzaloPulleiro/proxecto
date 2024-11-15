package com.app.comocomo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController


class RegistrarUsuarioFragment : Fragment() {

    private lateinit var registrarse: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_registrar_usuario, container, false)

        registrarse = view.findViewById(R.id.next_button)


        registrarse.setOnClickListener({
            view.findNavController().navigate(R.id.action_registrarUsuarioFragment_to_iniciarSesionFragment)
        })

return view

    }


}