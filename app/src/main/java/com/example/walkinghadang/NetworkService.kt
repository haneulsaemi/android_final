package com.example.walkinghadang

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {

    @GET("{key}/{type}/{service}/{start}/{end}/")
    fun getJsonList(
        @Path("key") key: String,
        @Path("type") type: String = "json",
        @Path("service") service: String = "viewGil",
        @Path("start") startIndex: Int,
        @Path("end") endIndex: Int,
        @Query("GIL_NM") gilName: String? = null // 선택 항목
    ): Call<JsonResponse>
}