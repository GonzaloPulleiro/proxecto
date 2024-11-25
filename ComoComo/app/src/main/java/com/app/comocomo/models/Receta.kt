package com.app.comocomo.models

data class Receta (
    val id: Int,
    val nombre: String,
    val imagen: String,
    val tiempoPreparacion: String,
    val preparacion: String,
    val esFavorita: Boolean = false,
    val id_usuario: Int = 0
)