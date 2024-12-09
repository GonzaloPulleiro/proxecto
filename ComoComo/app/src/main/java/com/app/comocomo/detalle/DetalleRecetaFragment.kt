package com.app.comocomo.detalle

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentDetalleRecetaBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class DetalleRecetaFragment : Fragment() {

    private var _binding: FragmentDetalleRecetaBinding? = null
    private val binding: FragmentDetalleRecetaBinding
        get() = _binding!!

    private lateinit var recetaImage: ImageView
    private lateinit var recetaNombre: TextView
    private lateinit var recetaTiempo: TextView
    private lateinit var recetaIngredientes: TextView
    private lateinit var recetaPreparacion: TextView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var db: DatabaseHelper
    private var recetaId: Int = -1
    private var usuarioId: Int = -1
    private var creadorReceta: Int = -1
    private var isFavorito: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetalleRecetaBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener vistas
        recetaImage = _binding!!.recetaimage
        recetaNombre = _binding!!.recetanombre
        recetaTiempo = _binding!!.recetatiempo
        recetaIngredientes = _binding!!.recetaingredientes
        recetaPreparacion = _binding!!.recetapreparacion
        toolbar = _binding!!.toolbar

        // Iniciar DatabaseHelper
        db = DatabaseHelper(requireContext())

        usuarioId = obtenerUsuarioId()


        // Configurar la Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // Vuelve al fragmento anterior
        }

        // Obtener el ID de la receta desde los argumentos
        recetaId = arguments?.getInt("recetaId", -1) ?: -1


        // Si el ID de la receta es válido
        if (recetaId != -1) {
            // Obtener el creador de la receta
            creadorReceta = db.obtenerUsuarioReceta(recetaId)
            cargarReceta(recetaId)
        } else {
            Toast.makeText(requireContext(), "ID de receta no encontrado", Toast.LENGTH_SHORT).show()
        }

        // Configurar el menú de la toolbar
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu) // Infla el menú

                if(creadorReceta != usuarioId){
                    menu.findItem(R.id.edit).isVisible = false
                    menu.findItem(R.id.delete).isVisible = false
                }
                setFavoritoIcon(menu.findItem(R.id.fav)) // Llama a setFavoritoIcon para configurar el icono de favorito
            }
            // Manejar la selección del menú
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Maneja los eventos de selección del menú aquí
                return when (menuItem.itemId) {
                    R.id.fav -> {
                        toggleFavorito(menuItem) // Alternar favorito
                        true
                    }
                    R.id.menu -> {
                     muestraDatePicker()
                     true
                    }
                    R.id.edit -> {
                        editReceta(recetaId)
                        true
                    }
                    R.id.delete -> {
                       deleteReceta(recetaId)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED) // Vuelve a la vista actual


    }

    // Metodo para obtener el ID del usuario desde SharedPreferences desde otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }

    // Obtener la receta desde la base de datos
    @SuppressLint("DiscouragedApi")
    private fun cargarReceta(recetaId: Int) {
        val cursor = db.getRecetaConIngredientes(recetaId)
        // Manejar el cursor
        cursor?.use {
            // Verificar si hay al menos un registro
            if (it.moveToFirst()) {
                // Obtener los datos de la receta
                recetaNombre.text = it.getString(it.getColumnIndexOrThrow("nombre"))

                val tiempoPreparacion =
                    it.getString(it.getColumnIndexOrThrow("tiempo_preparacion"))
                recetaTiempo.text = "Tiempo de preparación: $tiempoPreparacion minutos"

                val preparacion = it.getString(it.getColumnIndexOrThrow("preparacion"))
                recetaPreparacion.text = preparacion

                // Cargar la imagen
                cargarImagenReceta(recetaId)

                // Obtener los ingredientes
                val ingredientes = StringBuilder()
                do {
                    val nombreIngrediente =
                        it.getString(it.getColumnIndexOrThrow("nombre_ingrediente"))
                    val cantidad = it.getString(it.getColumnIndexOrThrow("cantidad"))
                    ingredientes.append("- $cantidad $nombreIngrediente \n")
                } while (it.moveToNext())

                recetaIngredientes.text = ingredientes.toString()

                // Comprobar si la receta es favorita
                isFavorito = db.esFavorito(recetaId, usuarioId)

            } else {
                // Manejar el caso donde no se encontró la receta
                Toast.makeText(
                    requireContext(),
                    "No se encontraron detalles para esta receta",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun cargarImagenReceta(recetaId: Int) {
        // Obtener el nombre de la imagen desde la base de datos
        val imageName = db.getNombreImagenReceta(recetaId)

        if (imageName != null && imageName.isNotEmpty()) {
            // Verificar si existe un archivo con este nombre
            val imageFile = File(requireContext().filesDir, imageName)
            if (imageFile.exists()) {
                // Cargar imagen desde el archivo
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                recetaImage.setImageBitmap(bitmap)
                return
            }
        }
        // Si no hay imagen personalizada, buscar en los recursos
        val imageResId = requireContext().resources.getIdentifier(
            "receta$recetaId", "drawable", requireContext().packageName
        )
        if (imageResId != 0) {
            recetaImage.setImageResource(imageResId)
        } else {
            // Imagen predeterminada en caso de no encontrar ninguna
            recetaImage.setImageResource(R.drawable.default_image)
        }
    }

    private fun setFavoritoIcon(menuItem: MenuItem?) {
        menuItem?.icon = if (isFavorito) {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite2)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite)
        }
    }

    private fun toggleFavorito(menuItem: MenuItem) {
        if (isFavorito) {
            db.eliminarDeFavoritos(recetaId, usuarioId)
            isFavorito = false
            Toast.makeText(requireContext(), "Receta eliminada de favoritos", Toast.LENGTH_SHORT).show()
        } else {
            db.agregarFavoritos(recetaId, usuarioId)
            isFavorito = true
            Toast.makeText(requireContext(), "Receta añadida a favoritos", Toast.LENGTH_SHORT).show()
        }
        setFavoritoIcon(menuItem)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun muestraDatePicker() {
        // Crear un DatePickerDialog
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), null, year, month, day)

        // Accede al DatePicker del DatePickerDialog
        val datePicker = datePickerDialog.datePicker

        // Establecer fecha mínima
        val fechaMinima = calendar.timeInMillis
        datePicker.minDate = fechaMinima

        // Establecer listener para cuando se selecciona una fecha
        datePicker.init(year, month, day) { view, selectedYear, selectedMonth, selectedDay ->
            // Cuando el usuario seleccione un día, mostramos el diálogo
            val fechaSeleccionada = "$selectedDay/${selectedMonth + 1}/$selectedYear"

            val formato = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
            val fechaFormateada =
                try{
                    val fechaDate = formato.parse(fechaSeleccionada)
                    formato.format(fechaDate)
                } catch (e: Exception){
                    e.printStackTrace()
                    fechaSeleccionada
                }

            muestraDialogo(fechaFormateada)
            // Cerrar el DatePickerDialog de inmediato
            datePickerDialog.dismiss()
        }

        // Mostrar el DatePickerDialog
        datePickerDialog.show()

        // Obtener los botones del DatePickerDialog
        val cancelButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
        val okButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)

        // Hacer invisible el botón de "Aceptar"
        okButton.visibility = View.INVISIBLE

        // Hacer visible el botón de "Cancelar"
        cancelButton.visibility = View.VISIBLE
    }

    private fun muestraDialogo(fechaSeleccionada: String) {
        val tipoComida = arrayOf("Desayuno", "Almuerzo", "Snack", "Cena")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona el tipo de comida")
            .setItems(tipoComida) { dialog, which ->
                // Acciones a realizar cuando se selecciona un tipo de comida
                val tipoSeleccionado = tipoComida[which]
                addMenu(fechaSeleccionada, tipoSeleccionado)
            }
            .show()
    }

    private fun addMenu(fechaSeleccionada: String, tipoSeleccionado: String) {
        db.agregarMenu(usuarioId, recetaId, fechaSeleccionada, tipoSeleccionado)
        Toast.makeText(requireContext(), "Receta añadida al menú", Toast.LENGTH_SHORT).show()
    }

    private fun editReceta(recetaId: Int) {
        // Navegar al fragmento de edición de receta
        val bundle = Bundle().apply {
            // Pasa el ID de la receta
            putInt("recetaId", recetaId) // Pasa el ID de la receta
        }

        findNavController().navigate(R.id.action_detalleRecetaFragment_to_editarRecetaDetalleFragment, bundle)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun deleteReceta(id: Int) {
            val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Cancelar")
                builder.setMessage("¿Estás seguro de que quieres eliminar la receta?")

            builder.setPositiveButton("Sí") { _, _ ->
                db.eliminarReceta(id)
                Toast.makeText(requireContext(), "Receta eliminada", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }

            builder.setNegativeButton("No", null)
            builder.show()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



