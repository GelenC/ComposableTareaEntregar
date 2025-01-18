package com.example.composabletareaentregar.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDateTime
/*interfaz que contiene los métodos para interactuar con la tabla Usuario*/
@Dao
interface UsuarioDao {
    @Insert
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario LIKE :nombreBuscado")
    suspend fun verificarUsuario(nombreBuscado:String): Usuario

    @Query("SELECT * FROM usuarios WHERE correo LIKE :correo AND usuario NOT LIKE :usuarionombre")
    suspend fun verificarCorreo(correo: String, usuarionombre: String): Usuario

    @Query("UPDATE usuarios SET fechaInicio = :timestamp WHERE usuario LIKE :usuarionombre")
    suspend fun updateFecha(usuarionombre: String, timestamp: LocalDateTime)

    @Query("UPDATE usuarios SET numInicios = numInicios+1 WHERE usuario LIKE :usuarionombre")
    suspend fun updateNumInicios(usuarionombre: String)
}