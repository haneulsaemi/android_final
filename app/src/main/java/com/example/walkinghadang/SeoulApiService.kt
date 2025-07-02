package com.example.walkinghadang

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SeoulApiService {

    @GET("{key}/{type}/{service}/{start}/{end}/")
    fun getJsonList(
        @Path("key") key: String,
        @Path("type") type: String = "json",
        @Path("service") service: String = "viewGil",
        @Path("start") startIndex: Int,
        @Path("end") endIndex: Int,
        @Query("GIL_NM") gilName: String? = null,
        @Query("LV_CD") level: String? = null,
        @Query("REQ_TM") time: String? = null
    ): Call<JsonResponse>

}