package com.example.walkinghadang


data class myJsonRow(
    val MENU_SN: String,
    val GIL_NO: Double,
    val GIL_NM: String,
    val GIL_SBNM: String,
    val LV_CD: String,
    val GIL_EXPLN: String,
    val GIL_DTL_NM: String,
    val STRT_PSTN: String,
    val END_PSTN: String,
    val STMP_BOX_PSTN: String,
    val GIL_LEN: Double,
    val REQ_TM: String,
    val STMP_BOX_PSTN_2: String,
    val STMP_BOX_PSTN_3: String,
    val SEOUL_MAP_URL: String,
    val FILE_DOWNLOAD_PAGE_LINK: String
)
data class myViewGil(val row :  MutableList<myJsonRow>?)

data class JsonResponse(val viewGil : myViewGil)