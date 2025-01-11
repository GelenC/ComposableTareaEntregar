package com.example.composabletareaentregar

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = false) val usuario:String,
    val correo:String,
    val numInicios:Int,
    val fechaInicio:LocalDateTime?=null)
