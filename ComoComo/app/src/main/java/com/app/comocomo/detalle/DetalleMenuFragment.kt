package com.app.comocomo.detalle

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.adapter.MenuAdapter
import com.app.comocomo.databinding.FragmentDetalleMenuBinding
import com.app.comocomo.models.Receta

class DetalleMenuFragment : Fragment() {

    private var _binding: FragmentDetalleMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var selectedDate: String
    private lateinit var toolbar: Toolbar

    private lateinit var menuDiaText: TextView
    private lateinit var vistaDesayuno: RecyclerView
    private lateinit var vistaAlmuerzo: RecyclerView
    private lateinit var vistaSnack: RecyclerView
    private lateinit var vistaCena: RecyclerView


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetalleMenuBinding.inflate(inflater, container, false)
        val view = binding.root

        db = DatabaseHelper(requireContext())

        menuDiaText = view.findViewById(R.id.tv_menu_dia)
        vistaDesayuno = view.findViewById(R.id.receta_layout_desayuno)
        vistaAlmuerzo = view.findViewById(R.id.receta_layout_almuerzo)
        vistaSnack = view.findViewById(R.id.receta_layout_snack)
        vistaCena = view.findViewById(R.id.receta_layout_cena)


        toolbar = _binding!!.toolbar

        // Configurar la Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // Vuelve al fragmento anterior
        }

        // Obtener la fecha seleccionada desde el Bundle
        selectedDate = arguments?.getString("diaSeleccionado").toString()

        binding.tvMenuDia.text = "Menú para el día $selectedDate"


        val recetas = db.obtenerRecetasMenuPorDia(selectedDate, obtenerUsuarioId())

        // Cargar recetas en RecyclerViews
        // Filtrar recetas por tipo de comida
        val desayunoRecetas = recetas.filter { it.first == "Desayuno" }.map { it.second }
        val almuerzoRecetas = recetas.filter { it.first == "Almuerzo" }.map { it.second }
        val snackRecetas = recetas.filter { it.first == "Snack" }.map { it.second }
        val cenaRecetas = recetas.filter { it.first == "Cena" }.map { it.second }

        // Configurar RecyclerViews
        vistaDesayuno.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(desayunoRecetas) { receta ->
                eliminarRecetaDelMenu(receta, "Desayuno")  // Llamar la función de eliminación cuando se presiona el botón
            }
        }
        vistaAlmuerzo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(almuerzoRecetas) { receta ->
                eliminarRecetaDelMenu(receta, "Almuerzo")
            }
        }

        vistaSnack.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(snackRecetas) { receta ->
                eliminarRecetaDelMenu(receta, "Snack")
            }
        }

        vistaCena.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(cenaRecetas) { receta ->
                eliminarRecetaDelMenu(receta, "Cena")
            }
        }

        return view
    }

    // Metodo para obtener el ID del usuario desde SharedPreferences desde otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }

    private fun eliminarRecetaDelMenu(receta: Receta, tipoComida: String) {
        // Mostrar un diálogo de confirmación
        val msj =  android.app.AlertDialog.Builder(requireContext())
            msj.setTitle("Eliminar receta")
            msj.setMessage("¿Estás seguro de que quieres eliminar esta receta del menú?")

        msj.setPositiveButton("Sí") { _, _ ->
            db.eliminarRecetaDelMenu(receta.id, obtenerUsuarioId(), selectedDate, tipoComida)
            Toast.makeText(requireContext(), "Receta eliminada del menú", Toast.LENGTH_SHORT).show()
            actualizarVistaMenu()
        }

        msj.setNegativeButton("No", null)
        msj.show()

        // Actualizar la vista
        actualizarVistaMenu()
    }

    private fun actualizarVistaMenu() {
        // Obtener las recetas del menú para la fecha seleccionada
        var recetas = db.obtenerRecetasMenuPorDia(selectedDate, obtenerUsuarioId())
        // Filtrar recetas por tipo de comida
        val desayunoRecetas = recetas.filter { it.first == "Desayuno" }.map { it.second }
        val almuerzoRecetas = recetas.filter { it.first == "Almuerzo" }.map { it.second }
        val snackRecetas = recetas.filter { it.first == "Snack" }.map { it.second }
        val cenaRecetas = recetas.filter { it.first == "Cena" }.map { it.second }
        // Actualizar las vistas con las recetas correspondientes
        (vistaDesayuno.adapter as MenuAdapter).actualizarRecetas(desayunoRecetas)
        (vistaAlmuerzo.adapter as MenuAdapter).actualizarRecetas(almuerzoRecetas)
        (vistaSnack.adapter as MenuAdapter).actualizarRecetas(snackRecetas)
        (vistaCena.adapter as MenuAdapter).actualizarRecetas(cenaRecetas)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}