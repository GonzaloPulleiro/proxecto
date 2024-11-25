package com.app.comocomo.models

data class MenuItem(
    val idReceta: Long,
    val nombre: String,
    val tiempoPreparacion: Int,  // o String si prefieres formatearlo
    val imageUrl: String?,  // URL de la imagen (puede ser null si no tienes una)
    val dia: String,  // Formato "yyyy-MM-dd"
    val tipoComida: String  // Ej. Desayuno, Almuerzo, Cena, Snack
)
