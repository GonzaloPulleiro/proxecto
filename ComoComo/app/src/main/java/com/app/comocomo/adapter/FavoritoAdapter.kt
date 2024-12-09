package com.app.comocomo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import java.io.File


class FavoritoAdapter(private val context: Context, private var cursor: Cursor?, private val origenFragmentId: Int) : RecyclerView.Adapter<FavoritoAdapter.ViewHolder>() {

    private lateinit var db: DatabaseHelper

    // ViewHolder para cada elemento de la lista
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recetaImage: ImageView = view.findViewById(R.id.receta_image)
        val recetaNombre: TextView = view.findViewById(R.id.receta_nombre)
        val recetaTiempo: TextView = view.findViewById(R.id.receta_tiempo)
        val favButton: ImageButton = view.findViewById(R.id.fav)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_receta_favoritos, parent, false)
        return ViewHolder(view)
    }

    // Metodo para obtener el ID del usuario desde SharedPreferences desde otros fragmentos
    fun obtenerUsuarioId(): Int {
        val sharedPreferences = context.getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1) // Devuelve -1 si no está guardado
    }


    // Metodo para cargar los datos de la receta en el ViewHolder
    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val usuarioId = obtenerUsuarioId()

        if (cursor?.moveToPosition(position) == true) {
            // Obtener los datos de la receta
            val idColumnIndex = cursor!!.getColumnIndex("id")
            if (idColumnIndex != -1) {
                val id = cursor!!.getInt(idColumnIndex)
                val nombre = cursor?.getString(cursor!!.getColumnIndexOrThrow("nombre"))
                val tiempoPreparacion =
                    cursor?.getString(cursor!!.getColumnIndexOrThrow("tiempo_preparacion"))
                val imagenUri = cursor?.getString(cursor!!.getColumnIndexOrThrow("imagen"))
                // Asignar los datos a los elementos de la vista
                holder.recetaNombre.text = nombre
                holder.recetaTiempo.text = "Tiempo: $tiempoPreparacion min"

                cargarImagenReceta(context, holder.recetaImage, imagenUri, id)

                holder.favButton.setOnClickListener{
                    db = DatabaseHelper(context)
                    val recetaId = id
                    if (recetaId != -1){
                        db.eliminarDeFavoritos(recetaId, usuarioId)
                        Toast.makeText(context, "Receta eliminada de favoritos", Toast.LENGTH_SHORT).show()
                        // Recargar los favoritos
                        cursor = db.obtenerRecetasFavoritas(usuarioId)
                        // Actualizar el RecyclerView
                        notifyDataSetChanged()
                    }
                }

                // Agrega el listener para abrir los detalles
                holder.itemView.setOnClickListener {
                    // Pasamos el id de la receta entre fragmentos para poder abrirla en diferentes
                    val bundle = Bundle().apply {
                        putInt("recetaId", id)
                    }

                    try {
                        when (origenFragmentId) {
                            R.id.recetaFragment -> {
                                holder.itemView.findNavController().navigate(
                                    R.id.action_recetaFragment_to_detalleRecetaFragment,
                                    bundle
                                )
                            }

                            R.id.favoritosFragment -> {
                                holder.itemView.findNavController().navigate(
                                    R.id.action_favoritosFragment_to_detalleRecetaFragment,
                                    bundle
                                )
                            }
                            R.id.menuFragment -> {
                                holder.itemView.findNavController().navigate(
                                    R.id.action_detalleMenuFragment_to_detalleRecetaFragment,
                                    bundle
                                )
                            }
                            else -> {
                                Log.e(
                                    "FavoritoAdapter",
                                    "Origen de fragmento no válido: $origenFragmentId"
                                )
                            }
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e("FavoritoAdapter", "Error al navegar: ${e.message}")
                    }
                }
            }

        } else {
            holder.recetaNombre.text = "Receta no disponible"
            holder.recetaTiempo.text = ""
            holder.recetaImage.setImageResource(R.drawable.default_image)
        }

    }


    @SuppressLint("DiscouragedApi")
    fun cargarImagenReceta(context: Context, imageView: ImageView, imageName: String?, id: Int) {
        // Cargar la imagen de la receta desde el archivo almacenado
        if (imageName != null && imageName.isNotEmpty()) {
            val imageFile = File(context.filesDir, imageName)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(R.drawable.default_image)
            }
        } else {
            val imageResId = context.resources.getIdentifier(
                "receta$id",
                "drawable",
                context.packageName
            )
            if (imageResId != 0) {
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.default_image)
            }
        }
    }

    override fun getItemCount(): Int {
       return cursor?.count ?: 0
    }
    // Metodo para intercambiar el cursor y actualizar el RecyclerView
    fun swapCursor(newCursor: Cursor?) {
        if (cursor != null) {
            cursor?.close()
        }
        cursor = newCursor
        notifyDataSetChanged()
    }


}


