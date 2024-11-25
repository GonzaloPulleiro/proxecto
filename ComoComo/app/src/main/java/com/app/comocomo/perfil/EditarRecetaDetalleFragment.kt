package com.app.comocomo.perfil

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentEditarRecetaDetalleBinding
import com.app.comocomo.models.IngredienteCantidad
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Suppress("DEPRECATION")
class EditarRecetaDetalleFragment : Fragment() {

    private var _binding: FragmentEditarRecetaDetalleBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper
    private var recetaId: Int = -1
    private var imageUri: Uri? = null


    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditarRecetaDetalleBinding.inflate(inflater, container, false)

        db = DatabaseHelper(requireContext())

        // Obtener el ID de la receta desde los argumentos
        recetaId = arguments?.getInt("recetaId") ?: -1
        if(recetaId == -1){
            Toast.makeText(requireContext(), "Error al obtener el ID de la receta", Toast.LENGTH_SHORT).show()
        } else {
            // Log para asegurarte de que el ID se pasa correctamente
            Log.d("EditarRecetaDetalle", "Receta ID recibido: $recetaId")
        }

        cargarDatosReceta()

        // Confirmar edición cambios receta
        binding.editReceta.setOnClickListener {
            guardarCambios()
        }

        // Cancelar edición receta
        binding.cancelarSubida.setOnClickListener {
           cancelarEdicion()
        }
        return binding.root
    }

    // Cargar datos de la receta
    private fun cargarDatosReceta(){
        val receta = db.obtenerRecetaPorId(recetaId)
        receta?.let {
            binding.editTextNombreReceta.setText(it.nombre)
            binding.editTextTiempoPreparacion.setText(it.tiempoPreparacion)
            binding.editTextPreparacion.setText(it.preparacion)

            val editNombre = binding.editNombre
            val editTiempo = binding.editTiempo
            val editPreparacion = binding.editPreparacion
            val editImage = binding.editImage

            val rutaImagen = File(requireContext().filesDir, it.imagen)
            if (rutaImagen.exists()) {
                binding.imageViewReceta.setImageURI(Uri.fromFile(rutaImagen))
            } else {
                binding.imageViewReceta.setImageResource(R.drawable.default_image)
            }

            editImage.setOnClickListener{
                verificarPermisos()
            }


            editNombre.setOnClickListener {
                val nombreEditText = binding.editTextNombreReceta
                botonEditar(nombreEditText)

            }
            editTiempo.setOnClickListener {
                val nombreEditText = binding.editTextTiempoPreparacion
                botonEditar(nombreEditText)
            }
            editPreparacion.setOnClickListener {
                val nombreEditText = binding.editTextPreparacion
                botonEditar(nombreEditText)
            }

            cargarIngredientes(db.obtenerIngredientesDeReceta(recetaId))
        } ?: run {
            Toast.makeText(requireContext(), "Error al obtener la receta", Toast.LENGTH_SHORT).show()
        }

    }

    private fun cargarIngredientes(ingredientes: List<IngredienteCantidad>) {
        // Limpiar la vista
        binding.linearLayoutIngredientes.removeAllViews()

        if (ingredientes.isEmpty()) {
            // Si no hay ingredientes, muestra un campo vacío
            agregarCampoIngrediente()
        } else {
            // Si hay ingredientes, muestra los existentes
            ingredientes.forEach { ingrediente ->
                val ingredienteView = agregarVistaIngrediente()
                ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente)
                    .setText(ingrediente.nombre)
                ingredienteView.findViewById<EditText>(R.id.editTextCantidadIngrediente)
                    .setText(ingrediente.cantidad)

            }
        }
    }

    private fun agregarVistaIngrediente(): View {
        // Crear una vista para un ingrediente
        val ingredienteView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_ingrediente, binding.linearLayoutIngredientes, false)

        configurarBotonesIngredientes(ingredienteView)
        // Agregar la vista a la vista principal
        binding.linearLayoutIngredientes.addView(ingredienteView)
        // Aplicar animación
        ingredienteView.alpha = 0f
        ingredienteView.animate().alpha(1f).setDuration(300).start()
        return ingredienteView
    }

    private fun configurarBotonesIngredientes(ingredienteView: View){
        val removeButton = ingredienteView.findViewById<ImageButton>(R.id.removeButton)
        val addButton = ingredienteView.findViewById<ImageButton>(R.id.addIngrediente)
        val editButton = ingredienteView.findViewById<ImageButton>(R.id.editIngrediente)

        editButton.visibility = View.VISIBLE

        removeButton.setOnClickListener {
            // Aplicar animación de eliminación
            ingredienteView.animate().alpha(0f).setDuration(300).withEndAction {
                // Eliminar la vista
                binding.linearLayoutIngredientes.removeView(ingredienteView)
                // Si ya no hay campos, agrega uno nuevo vacío
                if (binding.linearLayoutIngredientes.childCount == 0) {
                    agregarCampoIngrediente()
                }
            }.start()
        }

        addButton.setOnClickListener {
            agregarVistaIngrediente()
        }

        editButton.setOnClickListener {
            val nombreEditText = ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente)
            val cantidadEditText = ingredienteView.findViewById<EditText>(R.id.editTextCantidadIngrediente)

            nombreEditText.isEnabled = true
            cantidadEditText.isEnabled = true
            nombreEditText.requestFocus()
        }
    }

    private fun guardarCambios() {
        val nombre = binding.editTextNombreReceta.text.toString().trim()
        val tiempoPreparacion = binding.editTextTiempoPreparacion.text.toString().trim()
        val preparacion = binding.editTextPreparacion.text.toString().trim()


        if (nombre.isEmpty() || tiempoPreparacion.isEmpty() || preparacion.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Por favor, complete todos los campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validar que al menos un ingrediente tiene nombre y cantidad
        val ingredientesValidos = (0 until binding.linearLayoutIngredientes.childCount).any { i ->
        val ingredienteView = binding.linearLayoutIngredientes.getChildAt(i)
        val nombreIngrediente =
            ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente).text.toString()
                .trim()

            nombreIngrediente.isNotEmpty()
        }

        if (!ingredientesValidos) {
            Toast.makeText(
                requireContext(),
                "La receta debe tener al menos un ingrediente válido",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val imagen = imageUri?.let { guardarImagen() } ?: obtenerImagenActual()

        db.actualizarReceta(recetaId, nombre, tiempoPreparacion, preparacion, imagen ?: "")
        guardarIngredientes()

        Toast.makeText(requireContext(), "Cambios guardados", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun guardarIngredientes() {
        val ingredientesActuales = db.obtenerIngredientesDeReceta(recetaId)
        val nuevosIngredientes = mutableListOf<IngredienteCantidad>()

        // Obtener los nuevos ingredientes
        for (i in 0 until binding.linearLayoutIngredientes.childCount) {
            // Obtener los datos del ingrediente
            val ingredienteView = binding.linearLayoutIngredientes.getChildAt(i)
            val nombre =
                ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente).text.toString()
                    .trim()
            val cantidad =
                ingredienteView.findViewById<EditText>(R.id.editTextCantidadIngrediente).text.toString()
                    .trim()

            if (nombre.isNotEmpty()) {
                nuevosIngredientes.add(IngredienteCantidad(nombre, cantidad))
            }
        }

        ingredientesActuales.forEach { actual ->
            if (nuevosIngredientes.none { it.nombre == actual.nombre }) {
                db.eliminarIngredienteDeReceta(recetaId, actual.nombre)
            }
        }

        nuevosIngredientes.forEach { actual ->
            if (ingredientesActuales.any { it.nombre == actual.nombre }) {
                db.actualizarCantidadIngrediente(recetaId, actual.nombre, actual.cantidad)
            } else {
                db.agregarIngredienteReceta(recetaId, actual.nombre, actual.cantidad)
            }
        }
    }

    private fun agregarCampoIngrediente(nombre: String = "", cantidad: String = "") {
        val inflater = LayoutInflater.from(context)
        val ingredienteView = inflater.inflate(R.layout.item_ingrediente, binding.linearLayoutIngredientes, false)

        val nombreEditText = ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente)
        val cantidadEditText = ingredienteView.findViewById<EditText>(R.id.editTextCantidadIngrediente)
        val removeButton = ingredienteView.findViewById<ImageButton>(R.id.removeButton)
        val addButton = ingredienteView.findViewById<ImageButton>(R.id.addIngrediente)
        val editButton = ingredienteView.findViewById<ImageButton>(R.id.editIngrediente)

        editButton.visibility = View.VISIBLE

        nombreEditText.setText(nombre)
        cantidadEditText.setText(cantidad)

        removeButton.setOnClickListener {
            binding.linearLayoutIngredientes.removeView(ingredienteView)

            // Si ya no hay campos, agrega uno nuevo vacío
            if (binding.linearLayoutIngredientes.childCount == 0) {
                agregarCampoIngrediente()
            }
        }
        addButton.setOnClickListener {
            agregarVistaIngrediente()
        }
        editButton.setOnClickListener {
            val nombreEditText = ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente)
            val cantidadEditText = ingredienteView.findViewById<EditText>(R.id.editTextCantidadIngrediente)

            nombreEditText.isEnabled = true
            cantidadEditText.isEnabled = true

            nombreEditText.requestFocus()

        }

        binding.linearLayoutIngredientes.addView(ingredienteView)
    }

    private fun botonEditar(nombreEditText: EditText){
        nombreEditText.isEnabled = true
        val text = nombreEditText.text.toString().trim()
        val cursorPosition = if(text.isNotEmpty()) {
            text.length
        } else {
            0
        }
        // Colocar el cursor al final del texto
        nombreEditText.requestFocus()
        nombreEditText.setSelection(cursorPosition)

        mostrarTeclado(nombreEditText)
    }

    private fun mostrarTeclado(nombreEditText: EditText){

        val imm = nombreEditText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(nombreEditText, InputMethodManager.SHOW_IMPLICIT)

    }

    private fun verificarPermisos(){
        // Pedir permisos
        val permisos = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        // Comprobar si los permisos ya están otorgados
        if (permisos.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }) {
            mostrarOpcionesSeleccion()
        } else {
            requestPermissions(permisos, 101)
        }
    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Manejar los resultados de los permisos
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Comprobar si los permisos fueron otorgados
        if (requestCode == 101 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            mostrarOpcionesSeleccion()
        } else {
            Toast.makeText(requireContext(), "Debe otorgar permisos para continuar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarOpcionesSeleccion() {
        val opciones = arrayOf("Tomar una foto", "Seleccionar de la galería")

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una opción")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirCamara() // Llamar a la cámara
                    1 -> abrirGaleria() // Llamar a la galería
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirCamara(){
        // Crear archivo de imagen
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(requireContext().packageManager) != null){
            val photoFile = crearArchivoDeImagen()
            if(photoFile != null && imageUri != null){
                // Asignar la URI del archivo a la variable imageUri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                cameraLauncher.launch(intent)
            } else {
                Toast.makeText(requireContext(), "No se pudo crear el archivo para la imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let { binding.imageViewReceta.setImageURI(it)} ?: run {
                Toast.makeText(requireContext(), "La URI de la imagen es nula", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No se ha tomado ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            val uri = result.data?.data
            if(uri != null){
                imageUri = uri
                binding.imageViewReceta.setImageURI(uri)
            }
        }
    }

    private fun guardarImagenLocal(uri: Uri, fileName: String) {
        try {
            // Copiar la imagen seleccionada a un archivo temporal
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            // Crear el archivo
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)

            // Copiar el contenido del inputStream al outputStream
            val buffer = ByteArray(1024)
            var length = 0
            while (inputStream?.read(buffer).also { if (it != null) {  length = it  } } != -1) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream?.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun crearArchivoDeImagen(): File? {
        val directorio = requireContext().getExternalFilesDir(null)
        return try{
            File.createTempFile("img_", ".png", directorio). apply {
                imageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", this)
            }
        } catch (e: IOException){
            e.printStackTrace()
            null
        }

    }

    private fun guardarImagen(): String? {
        return if (imageUri != null) {
            val nuevoNombreImagen = "receta_${System.currentTimeMillis()}.png"
            guardarImagenLocal(imageUri!!, nuevoNombreImagen)
            nuevoNombreImagen
        } else {
            null
        }
    }

    private fun obtenerImagenActual(): String? {
        val receta = db.obtenerRecetaPorId(recetaId)
        return receta?.imagen
    }

    private fun cancelarEdicion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cancelar")
        builder.setMessage("¿Estás seguro de que quieres cancelar la edición de esta receta?")

        builder.setPositiveButton("Sí") { _, _ ->
            requireActivity().supportFragmentManager.popBackStack()
        }

        builder.setNegativeButton("No", null)
        builder.show()
    }

    override fun onDestroyView() {
        val tieneIngredienteValido = (0 until binding.linearLayoutIngredientes.childCount).any { i ->
            val ingredienteView = binding.linearLayoutIngredientes.getChildAt(i)
            val nombreIngrediente =
                ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente).text.toString().trim()

            nombreIngrediente.isNotEmpty()
        }

        if (!tieneIngredienteValido) {
            Toast.makeText(requireContext(), "La receta debe tener al menos un ingrediente válido", Toast.LENGTH_SHORT).show()
        }

        super.onDestroyView()
    }

}