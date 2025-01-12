package com.example.composabletareaentregar.Retrofit

import com.google.gson.annotations.SerializedName

data class DogResponse(@SerializedName("status") var status:String,
                       @SerializedName("message")var message:List<String>)
