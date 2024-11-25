package com.app.comocomo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.R
import com.app.comocomo.models.Usuario

class UsuarioAdapter(private var usuarios: List<Usuario>, private val eliminarUsuario: (Usuario) -> Unit
): RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {



    inner  class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombre)
        val emoticonoImageView: ImageView = itemView.findViewById(R.id.imageViewEmoticon)
        val botonEliminar: ImageButton = itemView.findViewById(R.id.botonEliminar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): UsuarioAdapter.UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)

    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        // Obtener el usuario en la posición actual
        val usuario = usuarios[position]
        // Asignar los datos del usuario a las vistas
        holder.nombreTextView.text = usuario.nombre
        holder.emoticonoImageView.setImageResource(R.drawable.ic_user) // Aquí puedes poner un emoticono o imagen

        // Configurar el botón de eliminar
        holder.botonEliminar.setOnClickListener {
            // Mostrar un diálogo de confirmación
            val context = holder.itemView.context

            AlertDialog.Builder(context).apply {
                setTitle("Eliminar usuario")
                setMessage("¿Estás seguro de eliminar a ${usuario.nombre}?")
                setPositiveButton("Aceptar") { _, _ ->
                    eliminarUsuario(usuario)
                    Toast.makeText(context, "Usuario eliminado con éxito", Toast.LENGTH_SHORT)
                        .show()
                }
                setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                create().show()
            }
        }
    }

        override fun getItemCount(): Int {
            return usuarios.size
        }

        fun actualizarUsuarios(nuevosUsuarios: List<Usuario>) {
            this.usuarios = nuevosUsuarios
            notifyDataSetChanged()
        }

    }


