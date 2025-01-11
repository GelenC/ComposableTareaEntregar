package com.example.composabletareaentregar

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDateTime

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario LIKE :nombreBuscado")
    suspend fun verificarUsuario(nombreBuscado:String):Usuario

    @Query("UPDATE usuarios SET fechaInicio = :timestamp WHERE usuario LIKE :usuarionombre")
    fun updateFecha(usuarionombre: String, timestamp: LocalDateTime)

    @Query("UPDATE usuarios SET numInicios = numInicios+1 WHERE usuario LIKE :usuarionombre")
    fun updateNumInicios(usuarionombre: String)
}