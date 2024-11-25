package com.app.comocomo.models

data class Ingrediente(
    val id: Int,
    val nombre: String,
    val cantidad: String,
    val isNuevo: Boolean = false,
    val isEliminado: Boolean = false
)
