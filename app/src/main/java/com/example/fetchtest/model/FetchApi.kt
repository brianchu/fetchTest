package com.example.fetchtest.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface FetchApi {
    @GET("hiring.json")
    suspend fun getItems() : List<Item>
}

private const val URL = "https://fetch-hiring.s3.amazonaws.com"

object RetrofitInstance {
    val api : FetchApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FetchApi::class.java)
    }
}