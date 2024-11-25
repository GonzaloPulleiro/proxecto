package com.app.comocomo.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentInicialBinding


class InicialFragment : Fragment() {
    private var _binding: FragmentInicialBinding? = null
    private val binding: FragmentInicialBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInicialBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.iniciar.setOnClickListener({
            findNavController().navigate(R.id.action_inicialFragment_to_iniciarSesionFragment)
        })

        binding.registrar.setOnClickListener({
            findNavController().navigate(R.id.action_inicialFragment_to_registrarUsuarioFragment)
        })


        return view

    }


}