package com.app.comocomo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.app.comocomo.models.IngredienteCantidad
import com.app.comocomo.models.Receta
import com.app.comocomo.models.Usuario
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var mDatabase: SQLiteDatabase? = null

    companion object {
        private const val DATABASE_NAME =  "gestion_recetas.db" // Nombre del archivo de la base de datos
        private const val DATABASE_VERSION = 1

        private const val TABLE_FAVORITOS = "favoritas"
        private const val TABLE_RECETAS = "receta"
        private const val TABLE_INGREDIENTE = "ingrediente"
        private const val TABLE_RECETAINGREDIENTE = "receta_ingredientes"
        private const val TABLE_MENUSEMANAL = "menu_semanal"
        private const val TABLE_USUARIO = "usuario"
    }

    // Metodos para abrir y copiar la base de datos en la app

    @Throws(IOException::class)
    fun abrirBaseDeDatos() {
        if (!baseDeDatosExiste()) {
            this.readableDatabase // Crea el archivo en la ruta
            copiarBaseDeDatos()
        }
        mDatabase = SQLiteDatabase.openDatabase(
            context.getDatabasePath(DATABASE_NAME).path,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
    }

    private fun baseDeDatosExiste(): Boolean {
        val archivo = context.getDatabasePath(DATABASE_NAME)
        val exists = archivo.exists()
        return exists
    }

    @Throws(IOException::class)
    private fun copiarBaseDeDatos() {
        // Abre el archivo de la base de datos
        val input: InputStream = context.assets.open(DATABASE_NAME)

        val archivoSalida = context.getDatabasePath(DATABASE_NAME).path
        val output: OutputStream = FileOutputStream(archivoSalida)

        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }

        output.flush()
        output.close()
        input.close()
        // Log para verificar si la copia fue exitosa
        Log.d("DatabaseHelper", "Base de datos copiada correctamente a: $archivoSalida")
    }

    private fun verificarBaseDeDatos() {
        // Si la base de datos no está abierta, la abre
        if (mDatabase == null || !mDatabase!!.isOpen) {
            abrirBaseDeDatos()
        }
    }

    /** Gestión de usuarios **/

    // Metodo para usar al iniciar la app si el usuario inicia sesión antes de registrarse
    fun obtenerUsuariosRegistrados(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO", null)

        if (cursor.moveToFirst()){
            // Recorremos el cursor y añadimos los usuarios a la lista
            do{
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1
                usuarios.add(Usuario(id, nombre, esAdmin.toString()))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return usuarios
    }

    // Se obtienen todos los usuarios que no son administrador
    fun obtenerUsuariosParaEliminar(): List<Usuario>{
        val usuarios = mutableListOf<Usuario>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE esAdmin = 0", null)

        if (cursor.moveToFirst()){
            do{
                // Recorremos el cursor y añadimos los usuarios a la lista
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                usuarios.add(Usuario(id, nombre, "", "", false))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return usuarios
    }

    fun eliminarUsuario(id: Int): Int{
        val db = this.writableDatabase
        return db.delete(TABLE_USUARIO, "id = ?", arrayOf(id.toString()))
    }

    // Métodos para registrar usuario
    fun registrarUsuarioBD(nombre: String, email: String, contraseña: String): Long {
        val db = this.writableDatabase
        val esAdmin = if (nombre == "admin") 1 else 0
        val values = ContentValues().apply {
            // Asignamos los valores a los campos de la tabla
            put("nombre", nombre)
            put("email", email)
            put("contraseña", contraseña)
            put("esAdmin", esAdmin)
        }
        return db.insert(TABLE_USUARIO, null, values)
    }

    // Verificar si ya existe un administrador
    fun existeAdmin():Boolean{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE nombre = ?", arrayOf("admin"))
        val existe = cursor.count > 0 // Si el contador es mayor que 0, existe un administrador
        cursor.close()
        return existe
    }

    // Verificar si el email ya está registrado
    fun emailYaRegistrado(email:String):Boolean{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE email =?", arrayOf(email))
        val existe = cursor.count > 0 // Si el contador es mayor que 0, existe un administrador
        cursor.close()
        return existe
    }

    // Verificar si el nombre y la contraseña ya están registrados
    fun obtenerUsuarioPorNombreYContraseña(nombre:String, contraseña: String): Usuario? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE nombre = ? AND contraseña =?", arrayOf(nombre, contraseña))
        var usuario: Usuario? = null

        if(cursor.moveToFirst()){
            // Obtenemos los datos del usuario
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña"))
            val esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1
            // Creamos el objeto usuario
            usuario = Usuario(id, nombre, email, contraseña, esAdmin)
        }
        cursor.close()
        return usuario
    }

    fun obtenerUsuariosPorNombre(nombre: String): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE nombre = ?", arrayOf(nombre))

        if (cursor.moveToFirst()){
            do{
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña"))
                val esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1
                usuarios.add(Usuario(id, nombre, email, contraseña, esAdmin))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return usuarios
    }


    // Obtener el id del usuario cuando se logea
    fun obtenerUsuarioPorId(usuarioID: Int): Usuario? {
        val db = this.readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE id =?", arrayOf(usuarioID.toString()))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña"))
            val esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1
            val usuario = Usuario(id, nombre, email, contraseña, esAdmin)
            cursor.close()
            return usuario
        } else{
            cursor.close()
            return null
        }

    }

    /** Gestión de recetas **/

    // Métodos para obtener y filtrar recetas
    fun obtenerRecetas(): Cursor {
        verificarBaseDeDatos()
        val query = "SELECT * FROM $TABLE_RECETAS ORDER BY id ASC"
        return mDatabase!!.rawQuery(query, null)
    }
    fun obtenerRecetasOrdenadasPorTiempoPreparacion(order: String): Cursor {
        verificarBaseDeDatos()
        val query = "SELECT * FROM $TABLE_RECETAS ORDER BY tiempo_preparacion $order"
        return mDatabase!!.rawQuery(query, null)

    }
    fun obtenerRecetasOrdenadasAlfabeticamente(): Cursor {
        verificarBaseDeDatos()
        val query = "SELECT * FROM $TABLE_RECETAS ORDER BY nombre ASC"
        return mDatabase!!.rawQuery(query, null)

    }
    fun obtenerRecetaPorNombre(nombre: String): Cursor {
        verificarBaseDeDatos()
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_RECETAS WHERE nombre LIKE ?", arrayOf("%$nombre%"))
    }
    fun getNombreImagenReceta(recetaId: Int) : String? {
        val db = readableDatabase
        val cursor = db.query(TABLE_RECETAS, arrayOf("imagen"), "id = ?", arrayOf(recetaId.toString()), null, null, null)

        var nombreImagen: String? = null
        if (cursor.moveToFirst()) {
            nombreImagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
        }
        cursor.close()
        return nombreImagen
    }

    // Metodo para DetalleReceta

    // Obtener la receta completa
    fun getRecetaConIngredientes(recetaId: Int): Cursor? {
        verificarBaseDeDatos()
        return mDatabase!!.rawQuery("""
        SELECT r.id, r.nombre, r.tiempo_preparacion, r.preparacion, r.imagen, 
               i.nombre AS nombre_ingrediente, ri.cantidad 
        FROM $TABLE_RECETAS r
        JOIN $TABLE_RECETAINGREDIENTE ri ON r.id = ri.id_receta
        JOIN $TABLE_INGREDIENTE i ON ri.id_ingrediente = i.id
        WHERE r.id = ?
    """, arrayOf(recetaId.toString()))
    }
    // Obtener el id del usuario de la receta
    fun obtenerUsuarioReceta(recetaId: Int): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id_usuario FROM $TABLE_RECETAS WHERE id = ?", arrayOf(recetaId.toString()))
        if (cursor.moveToFirst()) {
            return cursor.getInt(0)
        } else {
            return -1
        }
    }

    /** Gestión de favoritos **/

    fun esFavorito(recetaId: Int, usuarioID: Int): Boolean {
        verificarBaseDeDatos()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FAVORITOS WHERE id_receta = ? AND id_usuario = ?", arrayOf(recetaId.toString(), usuarioID.toString()))
        val esFavorito = cursor.count > 0
        cursor.close()
        return esFavorito
    }

    fun agregarFavoritos(recetaId: Int, usuarioID: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_receta", recetaId)
            put("id_usuario", usuarioID)
        }
        db.insert(TABLE_FAVORITOS, null, values)
    }

    fun eliminarDeFavoritos(recetaId: Int, usuarioID: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_FAVORITOS, "id_receta = ? AND id_usuario = ?", arrayOf(recetaId.toString(), usuarioID.toString()))
    }

    fun obtenerRecetasFavoritas(idUsuario: Int): Cursor? {
        verificarBaseDeDatos()
        val db = this.readableDatabase
        return db.rawQuery(
                "SELECT * FROM $TABLE_FAVORITOS INNER JOIN $TABLE_RECETAS ON $TABLE_FAVORITOS.id_receta = $TABLE_RECETAS.id WHERE $TABLE_FAVORITOS.id_usuario = ?",
                arrayOf(idUsuario.toString()))
    }

    // Obtener recetas favoritas filtradas por nombre
    fun obtenerRecetasFavoritasPorNombre(nombre: String): Cursor? {
        verificarBaseDeDatos()
        return mDatabase!!.rawQuery(
            """
            SELECT * FROM $TABLE_FAVORITOS 
            INNER JOIN $TABLE_RECETAS ON $TABLE_FAVORITOS.id_receta = $TABLE_RECETAS.id 
            WHERE $TABLE_RECETAS.nombre LIKE ?
        """,
            arrayOf("%$nombre%")
        )
    }

    // Obtener recetas favoritas ordenadas por tiempo de preparación
    fun obtenerRecetasFavoritasOrdenadasPorTiempoPreparacion(order: String): Cursor? {
        verificarBaseDeDatos()
        return mDatabase!!.rawQuery(
            """
            SELECT * FROM $TABLE_FAVORITOS 
            INNER JOIN $TABLE_RECETAS ON $TABLE_FAVORITOS.id_receta = $TABLE_RECETAS.id 
            ORDER BY $TABLE_RECETAS.tiempo_preparacion $order
        """,
            null
        )
    }

    // Obtener recetas favoritas ordenadas alfabéticamente
    fun obtenerRecetasFavoritasOrdenadasAlfabeticamente(): Cursor? {
        verificarBaseDeDatos()
        return mDatabase!!.rawQuery(
            """
            SELECT * FROM $TABLE_FAVORITOS 
            INNER JOIN $TABLE_RECETAS ON $TABLE_FAVORITOS.id_receta = $TABLE_RECETAS.id 
            ORDER BY $TABLE_RECETAS.nombre ASC
        """,
            null
        )
    }


    /** Gestión recetas usuario **/
    fun obtenerTotalRecetasUsuario(usuarioID: Int): Int{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_RECETAS WHERE id_usuario = ?", arrayOf(usuarioID.toString()))
        var cantidad = 0
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0)
        }
        cursor.close()
        return cantidad
    }

    // subir receta
    fun agregarReceta(nombre:String, tiempoPreparacion: String, ingredientes: List<Pair<String, String>>, preparacion: String, usuarioID: Int, imagen: String) {
        val db = this.writableDatabase
        db.beginTransaction()

        try{
            val values = ContentValues().apply {
                put("nombre", nombre)
                put("tiempo_preparacion", tiempoPreparacion)
                put("preparacion", preparacion)
                put("id_usuario", usuarioID)
                put("imagen", imagen)
            }
            val recetaId = db.insert("$TABLE_RECETAS", null, values)

            if (recetaId == -1L) {
                throw Exception("Error al insertar la receta")
            }
            // insertar ingredientes en ingredientes y receta ingredientes
            for ((nombreIngrediente, cantidad) in ingredientes) {
                var ingredienteId: Long = -1

                val consulta = "SELECT id FROM $TABLE_INGREDIENTE WHERE nombre = ?"
                val cursor = db.rawQuery(consulta, arrayOf(nombreIngrediente))

                if (cursor.moveToFirst()) {
                    ingredienteId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                }
                cursor.close()

                // si el ingrediente no existe, lo añadimos y obtenemos el id
                if (ingredienteId == -1L) {
                    val ingredientesValues = ContentValues().apply {
                        put("nombre", nombreIngrediente)
                    }
                    ingredienteId = db.insert(TABLE_INGREDIENTE, null, ingredientesValues)

                    if (ingredienteId == -1L) {
                        throw Exception("No se pudo insertar el ingrediente: $nombreIngrediente")
                    }
                }
                // insertar relación en recetaIngredientes
                val recetaingredientesValues = ContentValues().apply {
                    put("id_receta", recetaId)
                    put("id_ingrediente", ingredienteId)
                    put("cantidad", cantidad)
                }
                db.insertWithOnConflict(TABLE_RECETAINGREDIENTE, null, recetaingredientesValues, SQLiteDatabase.CONFLICT_IGNORE)
                }

                db.setTransactionSuccessful()

            }catch (e: Exception){
                Log.e("DatabaseHelper", "Error al insertar la receta y sus ingredientes: ${e.message}")
        } finally {
            db.endTransaction()
        }

    }

    fun eliminarReceta(id: Int) : Boolean{
        val db = this.writableDatabase
        val result = db.delete(TABLE_RECETAS, "id = ?", arrayOf(id.toString()))
        return result > 0
    }

    fun obtenerRecetasPorUsuario(idUsuario: Int): Cursor {
        val db = this.readableDatabase
        val query = "SELECT * FROM receta WHERE id_usuario = ? ORDER BY id DESC"
        return db.rawQuery(query, arrayOf(idUsuario.toString()))
    }

    fun obtenerRecetaPorId(recetaId: Int): Receta? {
        val db = readableDatabase
        val cursor = db.query(TABLE_RECETAS, arrayOf("id", "nombre", "tiempo_preparacion", "preparacion", "imagen"), "id = ?", arrayOf(recetaId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            val receta = Receta(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                tiempoPreparacion = cursor.getString(2),
                preparacion = cursor.getString(3),
                imagen = cursor.getString(4) ?: ""
            )
            cursor.close()
            return receta
        }
        cursor.close()
        return null
    }

    fun obtenerIngredientesDeReceta(recetaId: Int): List<IngredienteCantidad>{
        val db = readableDatabase
        val ingredientes = mutableListOf<IngredienteCantidad>()
        val cursor = db.rawQuery("SELECT i.nombre, ri.cantidad FROM $TABLE_RECETAINGREDIENTE ri INNER JOIN $TABLE_INGREDIENTE i ON ri.id_ingrediente = i.id WHERE ri.id_receta = ?", arrayOf(recetaId.toString()))
        if (cursor.moveToFirst()){
            do{
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val cantidad = cursor.getString(cursor.getColumnIndexOrThrow("cantidad"))
                ingredientes.add(IngredienteCantidad(nombre, cantidad))
            } while (cursor.moveToNext())
            }
        cursor.close()
        return ingredientes.distinctBy { it.nombre }
        }

    fun obtenerIdIngrediente(nombre:String): Int? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM $TABLE_INGREDIENTE WHERE nombre = ?", arrayOf(nombre))
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            cursor.close()
            id // Devuelve el id del ingrediente
        } else {
            cursor.close()
            null
        }
    }

    fun insertarIngrediente(nombre: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
        }
        db.insert(TABLE_INGREDIENTE, null, values)

        val cursor = db.rawQuery("SELECT last_insert_rowid()", null) // Obtener el último id insertado
        cursor.moveToFirst()
        val id = cursor.getInt(0)
        cursor.close()
        return id

    }

    fun agregarIngredienteReceta(recetaId: Int, nombreIngrediente: String, cantidadIngrediente: String) {
        val db = this.writableDatabase
        var ingredienteId =
            obtenerIdIngrediente(nombreIngrediente) ?: insertarIngrediente(nombreIngrediente)

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_RECETAINGREDIENTE WHERE id_receta = ? AND id_ingrediente = ?",
            arrayOf(recetaId.toString(), ingredienteId.toString())
        )
        if (cursor.count == 0) {
            val values = ContentValues().apply {
                put("id_receta", recetaId)
                put("id_ingrediente", ingredienteId)
                put("cantidad", cantidadIngrediente)
            }
            db.insert(TABLE_RECETAINGREDIENTE, null, values)
        }
    }

    fun eliminarIngredienteDeReceta(recetaId: Int, nombreIngrediente: String){
        val db = this.writableDatabase
        val whereClause = "id_receta = ? AND id_ingrediente = (SELECT id FROM $TABLE_INGREDIENTE WHERE nombre = ?)" // Subconsulta para obtener el id del ingrediente

        // Eliminar la relación
        db.delete(TABLE_RECETAINGREDIENTE, whereClause, arrayOf(recetaId.toString(), nombreIngrediente))
    }

    fun actualizarCantidadIngrediente(recetaId: Int, nombreIngrediente: String, cantidad: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("cantidad", cantidad)
        }
        val whereClause = "id_receta = ? AND id_ingrediente = (SELECT id FROM $TABLE_INGREDIENTE WHERE nombre = ?)" // Subconsulta para obtener el id del ingrediente
        // Actualiza la cantidad
        db.update(TABLE_RECETAINGREDIENTE, values, whereClause, arrayOf(recetaId.toString(), nombreIngrediente))
    }

    fun actualizarReceta(id: Int, nombre: String, tiempoPreparacion: String, preparacion: String, imagen: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("tiempo_preparacion", tiempoPreparacion)
            put("preparacion", preparacion)
            if (imagen != null) put("imagen", imagen)
    }
        db.update(TABLE_RECETAS, values, "id = ?", arrayOf(id.toString()))
    }


    /** Gestión del menú **/
    fun agregarMenu(idUsuario: Int, idReceta: Int, dia: String, tipoComida: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("id_receta", idReceta)
            put("dia", dia)
            put("tipo_comida", tipoComida)
        }
        val result = db.insert(TABLE_MENUSEMANAL, null, values)

        if (result == -1L) {
            Toast.makeText(context, "Error al insertar el menú semanal", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Menú semanal insertado correctamente", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun obtenerRecetasMenuPorDia(dia: String, idUsuario: Int): List<Pair<String, Receta>> {
        val recetas = mutableListOf<Pair<String, Receta>>()
        val db = this.readableDatabase

        val query =
            "SELECT tipo_comida, id_receta FROM menu_semanal WHERE dia = ? AND id_usuario = ?"
        val cursor = db.rawQuery(query, arrayOf(dia, idUsuario.toString()))

        try {
            if (cursor.moveToFirst()) {
                do {
                    val tipoComida = cursor.getString(cursor.getColumnIndexOrThrow("tipo_comida"))
                    val idReceta = cursor.getInt(cursor.getColumnIndexOrThrow("id_receta"))
                    val receta = obtenerRecetaPorId(idReceta) // Obtener receta por ID
                    if (receta != null) {
                        recetas.add(Pair(tipoComida, receta))
                    } else {
                        Log.w("DatabaseHelper", "Receta con ID $idReceta no encontrada")
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al obtener recetas: ${e.message}")
        } finally {
            cursor.close()

        }
            return recetas
    }

    fun eliminarRecetaDelMenu(idReceta: Int, idUsuario: Int, fecha: String, tipoComida: String) {
            val db = writableDatabase
            try {
                val rowsDeleted = db.delete(
                    "menu_semanal",  // Nombre de la tabla
                    "id_receta = ? AND id_usuario = ? AND dia = ? AND tipo_comida = ?", // Condición
                    arrayOf(idReceta.toString(), idUsuario.toString(), fecha, tipoComida) // Valores de la condición
                )

                if (rowsDeleted == 0) {
                    Log.e("DatabaseHelper", "No se encontró ninguna receta para eliminar.")
                } else {
                    Log.d("DatabaseHelper", "Receta eliminada correctamente.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException("Error al eliminar la receta del menú: ${e.message}")
            } finally {
                db.close() // Asegurar el cierre de la conexión
            }
        }



    // Metodos incluidos en la clase por usar SQLiteOpenHelper, pero no se usan en este caso
    override fun onCreate(p0: SQLiteDatabase?) {}

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

}