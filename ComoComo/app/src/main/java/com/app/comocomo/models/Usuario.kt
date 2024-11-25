package com.app.comocomo.models

data class Usuario(
    val id: Int = 0,
    val nombre: String = "",
    val email: String = "",
    val contraseña: String = "",
    val esAdmin: Boolean = false
)