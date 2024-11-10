package com.app.comocomo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toolbar
import androidx.navigation.findNavController


class IniciarSesionFragment : Fragment() {

    private lateinit var next: Button

  @SuppressLint("MissingInflatedId")
  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_iniciar_sesion, container, false)


      next = view.findViewById(R.id.next_button)


      next.setOnClickListener({
          view.findNavController().navigate(R.id.action_iniciarSesionFragment_to_inicioFragment)
      })



        return view

    }


}