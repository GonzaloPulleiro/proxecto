package com.app.comocomo.perfil

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.comocomo.DatabaseHelper
import com.app.comocomo.adapter.UsuarioAdapter
import com.app.comocomo.databinding.FragmentListaUsuariosBinding
import com.app.comocomo.models.Usuario

class ListaUsuariosFragment : Fragment() {

    private var _binding: FragmentListaUsuariosBinding? = null
    private val binding: FragmentListaUsuariosBinding
        get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: DatabaseHelper
    private lateinit var usuarioAdapter: UsuarioAdapter
    private lateinit var usuarios: List<Usuario>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListaUsuariosBinding.inflate(inflater, container, false)
        val view = binding.root

        // Iniciar bd
        db = DatabaseHelper(requireContext())

        toolbar = _binding!!.toolbar

        // Configurar la Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // Vuelve al fragmento anterior
        }

        //Vista de los datos
        recyclerView = binding.recyclerViewUsuarios
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Agregar un espacio entre los items
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
               outRect.bottom = 5  // Espacio entre los items
            }
        })

        // Obtener lista de usuarios
        usuarios = db.obtenerUsuariosParaEliminar()

        usuarioAdapter = UsuarioAdapter(usuarios) { usuario ->
            eliminarUsuariodeBD(usuario)
        }

        recyclerView.adapter = usuarioAdapter

        return view
    }

    private fun eliminarUsuariodeBD(usuario: Usuario) {
       val result = db.eliminarUsuario(usuario.id)
        if(result > 0){
           val nuevosUsuarios = db.obtenerUsuariosParaEliminar()
            usuarioAdapter.actualizarUsuarios(nuevosUsuarios)
        }
    }
}