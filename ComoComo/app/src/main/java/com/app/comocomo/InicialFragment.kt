package com.app.comocomo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController


class InicialFragment : Fragment() {

    private lateinit var iniciar: Button
    private lateinit var registrar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_inicial, container, false)

        iniciar = view.findViewById(R.id.iniciar)
        registrar = view.findViewById(R.id.registrar)


        iniciar.setOnClickListener({
            view.findNavController().navigate(R.id.action_inicialFragment_to_iniciarSesionFragment)
        })

        registrar.setOnClickListener({
            view.findNavController().navigate(R.id.action_inicialFragment_to_registrarUsuarioFragment)
        })


        return view

    }



}