package com.app.comocomo.principal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.ViewCompat
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentInicioBinding

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private var _binding: FragmentInicioBinding? = null
    private val binding: FragmentInicioBinding
        get() = _binding!!

private lateinit var info: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val view = binding.root

        // TooltipText donde muestro autor y finalidad de la aplicación
        info = binding.info

        ViewCompat.setTooltipText(info, "Aplicación creada por Gonzalo Pulleiro" +
                " para PFC DAM.\nNoviembre de 2024.")



        return view
    }

}
