package com.example.walkinghadang

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodRetrofit {

    companion object{
        private const val BASE_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/"
        val gson : Gson = GsonBuilder().setLenient().create()
        var foodApiService: FoodApiService
        val foodRetrofit : Retrofit
            get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        init {
            foodApiService = foodRetrofit.create(FoodApiService::class.java)
        }
    }
}