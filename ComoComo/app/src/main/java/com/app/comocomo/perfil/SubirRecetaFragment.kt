package com.app.comocomo.perfil

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.databinding.FragmentSubirRecetaBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class SubirRecetaFragment : Fragment() {
    private var _binding: FragmentSubirRecetaBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var imageViewRecetas: ImageButton
    private lateinit var ingredientesLayout: LinearLayout
    private lateinit var addReceta: Button
    private lateinit var cancelarSubida: Button
    private lateinit var addIngrediente: ImageButton
    private var imageUri: Uri? = null

    private var ingredientesList = mutableListOf<Pair<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSubirRecetaBinding.inflate(inflater, container, false)
        val view = binding.root

        db = DatabaseHelper(requireContext())


        imageViewRecetas = binding.imageViewReceta
        addReceta = binding.addReceta
        cancelarSubida = binding.cancelarSubida
        addIngrediente = binding.addIngrediente
        ingredientesLayout = binding.linearLayoutIngredientes


        // Añadir imagen a la receta
        imageViewRecetas.setOnClickListener{
            verificarPermisos()
        }
        //Añadir ingredientes a la receta
        addIngrediente.setOnClickListener {
            añadirIngrediente()
        }
        // Añadir receta
        addReceta.setOnClickListener {
            val nombre = binding.editTextNombreReceta.text.toString()
            val tiempoPreparacion = binding.editTextTiempoPreparacion.text.toString()
            val preparacion = binding.editTextPreparacion.text.toString()
            val usuarioId = obtenerUsuarioId()

            if (nombre.isEmpty() || tiempoPreparacion.isEmpty() || preparacion.isEmpty() || imageUri == null) {
                // Muestra un mensaje de error (puedes usar un Toast)
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validarIngredientes()){
                Toast.makeText(requireContext(), "Debes añadir ingrediente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageName = "receta_${System.currentTimeMillis()}.png"
            guardarImagenLocal(imageUri!!, imageName)

            // Agregar la receta a la base de datos
           db.agregarReceta(nombre, tiempoPreparacion, obtenerIngredientes(), preparacion, usuarioId, imageName)

            Toast.makeText(requireContext(), "Receta subida exitosamente", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_subirRecetaFragment_to_misRecetasFragment)
        }
        // Cancelar subida receta
        cancelarSubida.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cancelar")
                .setMessage("¿Estás seguro de que quieres cancelar la subida de la receta?")
                .setPositiveButton("Si") { _, _ ->
                    findNavController().navigate(R.id.action_subirRecetaFragment_to_misRecetasFragment)
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }
    // Metodo para obtener el ID del usuario desde SharedPreferences
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1)
    }

    // Metodo para añadir una fila más de ingrediente-cantidad para la receta
    private fun añadirIngrediente() {
        val vistaIngrediente = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_ingrediente, ingredientesLayout, false)

        val eliminar = vistaIngrediente.findViewById<ImageButton>(R.id.removeButton)
        eliminar.setOnClickListener {
            val index = binding.linearLayoutIngredientes.indexOfChild(vistaIngrediente)
            binding.linearLayoutIngredientes.removeView(vistaIngrediente)
            if (index in ingredientesList.indices) ingredientesList.removeAt(index)
        }

        val añadir = vistaIngrediente.findViewById<ImageButton>(R.id.addIngrediente)
        añadir.setOnClickListener {
            // efecto visual al añadir la fila
            vistaIngrediente.alpha = 0f
            vistaIngrediente.animate().alpha(1f).setDuration(300).start()
            añadirIngrediente()
        }

        binding.linearLayoutIngredientes.addView(vistaIngrediente)
        ingredientesList.add(Pair("", ""))
    }

    // Para que haya mínimo un ingrediente, la cantidad no es necesaria, el nombre si
    private fun validarIngredientes(): Boolean {
        for (i in 0 until   binding.linearLayoutIngredientes.childCount) {
            val ingredienteView = binding.linearLayoutIngredientes.getChildAt(i) as ViewGroup
            val nombre = ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente).text.toString()
            if (nombre.isEmpty()) return false
        }
        return true
    }

    // Añadir el ingrediente con la cantidad respectiva
    private fun obtenerIngredientes(): MutableList<Pair<String, String>> {
        val ingredientes = mutableListOf<Pair<String, String>>()
        for (i in 0 until binding.linearLayoutIngredientes.childCount) {
            val ingredienteView = binding.linearLayoutIngredientes.getChildAt(i) as ViewGroup
            val nombre = ingredienteView.findViewById<EditText>(R.id.editTextNombreIngrediente).text.toString()
            val cantidad = ingredienteView.findViewById<EditText>(R.id.editTextCantidadIngrediente).text.toString()
            ingredientes.add(Pair(nombre, cantidad))
        }
        return ingredientes
    }

    // Pedimos permisos al usuario para acceder a camara y galeria
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
        // Comprobar si hay una aplicación que pueda manejar la acción
            if(intent.resolveActivity(requireContext().packageManager) != null){
                val photoFile = crearArchivoDeImagen()
                // Comprobar si se pudo crear el archivo
                if(photoFile != null && imageUri != null){
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
                // Imagen guardada en imageUri
                imageUri?.let { imageViewRecetas.setImageURI(it)} ?: run {
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
                // Obtener la URI de la imagen seleccionada
                val uri = result.data?.data
                if(uri != null){
                    imageUri = uri
                    imageViewRecetas.setImageURI(uri)
                }
            }
        }
    private fun guardarImagenLocal(uri: Uri, fileName: String) {
        try {
            // Copiar la imagen seleccionada a un archivo temporal
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, fileName)
            if (file.exists()) file.delete()
            val outputStream = FileOutputStream(file)

            // Copiar el contenido del inputStream al outputStream
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream?.read(buffer).also { length = it  ?: -1 } != -1) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream?.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
    private fun crearArchivoDeImagen(): File? {
        // Crear un archivo temporal para guardar la imagen
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}