package com.example.walkinghadang

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApiService {
    @GET("getFoodNtrCpntDbInq02")
    fun getFoods(
        @Query("serviceKey") serviceKey: String,
        @Query("desc_kor") keyword: String?,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 100,
        @Query("type") type: String = "json"
    ): Call<FoodResponse>
}