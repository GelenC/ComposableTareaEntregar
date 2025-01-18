package com.example.composabletareaentregar.Retrofit

import com.google.gson.annotations.SerializedName
/* Modelo que representa la estructura de las respuestas de la API.
Contiene las mismas claves que el JSON devuelto por la API*/
data class DogResponse(@SerializedName("status") var status:String,
                       @SerializedName("message")var message:List<String>)
