package com.example.walkinghadang

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {

    companion object{
        private const val BASE_URL = "http://openapi.seoul.go.kr:8088/";
        var jsonSeoulApiService : SeoulApiService
        val jsonRetrofit : Retrofit
            get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        init{
            jsonSeoulApiService = jsonRetrofit.create(SeoulApiService::class.java)
        }
    }
}