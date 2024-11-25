package com.app.comocomo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.R

class RecetaEliminarAdapter(
    val context: Context,
    val cursor: Cursor,
    val onDeleteClickListener: (Int) -> Unit  // Listener para eliminar receta
) : RecyclerView.Adapter<RecetaEliminarAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreReceta: TextView = view.findViewById(R.id.textViewNombreReceta)
        val btnEliminar: ImageView = view.findViewById(R.id.botonEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_receta_eliminar, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        val id = cursor.getInt(cursor.getColumnIndex("id"))
        val nombre = cursor.getString(cursor.getColumnIndex("nombre"))

        holder.nombreReceta.text = nombre

        // Acción del botón de eliminar
        holder.btnEliminar.setOnClickListener {
            onDeleteClickListener(id)  // Llamamos al listener con el id de la receta
        }
    }

    override fun getItemCount(): Int {
        return cursor.count
    }
}
