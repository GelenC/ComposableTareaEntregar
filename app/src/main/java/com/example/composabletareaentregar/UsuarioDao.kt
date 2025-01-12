package com.example.composabletareaentregar

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDateTime
//Utilizamos el suspend para que se ejecuten fuera del hilo principal y no den error
@Dao
interface UsuarioDao {
    @Insert
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario LIKE :nombreBuscado")
    suspend fun verificarUsuario(nombreBuscado:String):Usuario

    @Query("UPDATE usuarios SET fechaInicio = :timestamp WHERE usuario LIKE :usuarionombre")
    suspend fun updateFecha(usuarionombre: String, timestamp: LocalDateTime)

    @Query("UPDATE usuarios SET numInicios = numInicios+1 WHERE usuario LIKE :usuarionombre")
    suspend fun updateNumInicios(usuarionombre: String)
}