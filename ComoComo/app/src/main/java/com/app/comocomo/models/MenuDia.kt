package com.app.comocomo.models

data class MenuDia(
    val dia: String = "", // Fecha del día en formato (por ejemplo: "2024-11-23")
    var recetaDesayuno: String? = null, // ID de la receta para el desayuno
    var recetaAlmuerzo: String? = null, // ID de la receta para el almuerzo
    var recetaSnack: String? = null,    // ID de la receta para el snack
    var recetaCena: String? = null      // ID de la receta para la cena
)
