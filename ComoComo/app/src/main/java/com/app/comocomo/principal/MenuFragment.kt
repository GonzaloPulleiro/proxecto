package com.app.comocomo.principal


import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentMenuBinding
import java.util.Locale


    class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var datePicker: DatePicker


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val view = binding.root


        db = DatabaseHelper(requireContext())
        datePicker = binding.datePicker


        // Para solo poder visualizar el menú una semana antes del día actual
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        datePicker.minDate = calendar.timeInMillis


        // Pulsamos sobre un día y nos lleva a la pantalla de detalle donde se verá ese día seleccionado
        // para eso tenemos que pasar el día al fragmento de detalle menu
        datePicker.setOnDateChangedListener{ _, year, monthOfYear, dayOfMonth ->
            val calendario = Calendar.getInstance().apply {
                set(year, monthOfYear, dayOfMonth)
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
            val diaSeleccionado = dateFormat.format(calendario.time)  // Obtener la fecha formateada con nombre de mes


            val bundle = Bundle().apply {
                putString("diaSeleccionado", diaSeleccionado)
            }

            findNavController().navigate(R.id.action_menuFragment_to_detalleMenuFragment, bundle)
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Liberar el binding
    }
}