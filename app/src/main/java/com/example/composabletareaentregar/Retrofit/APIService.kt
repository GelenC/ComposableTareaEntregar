package com.example.composabletareaentregar.Retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
        @GET
        suspend fun getPerrosRaza(@Url url: String): Response<DogResponse>
}