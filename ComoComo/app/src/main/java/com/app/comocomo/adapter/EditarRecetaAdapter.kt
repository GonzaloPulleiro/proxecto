package com.app.comocomo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.R

class EditarRecetaAdapter(
    val context: Context,
    val cursor: Cursor,
    val onItemClick: (Int) -> Unit // El callback que recibe el recetaId
) : RecyclerView.Adapter<EditarRecetaAdapter.RecetaViewHolder>() {

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreReceta: TextView = itemView.findViewById(R.id.textViewNombreReceta)
        val botonEditar: ImageView = itemView.findViewById(R.id.botonEditar)

        // Vincula los datos del cursor con los elementos de la vista
        fun bind(cursor: Cursor){
            val recetaId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            nombreReceta.text = nombre

            botonEditar.setOnClickListener {
                onItemClick(recetaId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_editar_receta, parent, false)
        return RecetaViewHolder(view)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        cursor.moveToPosition(position)
        holder.bind(cursor)

    }

    override fun getItemCount(): Int {
        return cursor.count
    }

}
