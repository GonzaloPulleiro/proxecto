package com.app.comocomo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.R
import com.app.comocomo.models.Receta
import java.io.File

class MenuAdapter(private var recetas: List<Receta>, private val onDeleteClick: (Receta) -> Unit) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreReceta: TextView = itemView.findViewById(R.id.receta_nombre)
        val tiempoReceta: TextView = itemView.findViewById(R.id.receta_tiempo)
        val imagenReceta: ImageView = itemView.findViewById(R.id.receta_image)
        val quitarReceta: ImageButton = itemView.findViewById(R.id.quitar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val receta = recetas[position]
        holder.nombreReceta.text = receta.nombre
        holder.tiempoReceta.text = "Tiempo de preparación: ${receta.tiempoPreparacion} minutos"

        cargarImagenReceta(holder.itemView.context, holder.imagenReceta, receta.imagen, receta.id)

        holder.quitarReceta.setOnClickListener {
            onDeleteClick(receta)
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("recetaId", receta.id)
            }
            holder.itemView.findNavController()
                .navigate(R.id.action_detalleMenuFragment_to_detalleRecetaFragment, bundle)
        }

    }

    override fun getItemCount() = recetas.size

    fun actualizarRecetas(nuevasRecetas: List<Receta>) {
        this.recetas = nuevasRecetas
        notifyDataSetChanged()  // Notifica al RecyclerView que los datos han cambiado
    }

    @SuppressLint("DiscouragedApi")
    private fun cargarImagenReceta(context: Context, imageView: ImageView, imageName: String?, recetaId: Int) {
        if (!imageName.isNullOrEmpty()) {
            // Si la imagen es un archivo local
            val imageFile = File(context.filesDir, imageName)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(R.drawable.default_image)  // Imagen por defecto si no se encuentra el archivo
            }
        } else {
            // Si la receta no tiene imagen, intenta cargarla desde los recursos
            val imageResId = context.resources.getIdentifier("receta$recetaId", "drawable", context.packageName)
            if (imageResId != 0) {
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.default_image)  // Imagen por defecto si no se encuentra en los recursos
            }
        }
    }
}