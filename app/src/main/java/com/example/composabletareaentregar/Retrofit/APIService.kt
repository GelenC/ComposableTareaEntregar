package com.example.composabletareaentregar.Retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url
/*Define una solicitud de red usando Retrofit, contiene un método
para realizar una llamada a la API.*/
interface APIService {
        @GET
        suspend fun getPerrosRaza(@Url url: String): Response<DogResponse>
}