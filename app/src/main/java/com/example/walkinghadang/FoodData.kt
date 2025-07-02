package com.example.walkinghadang

data class FoodItem(
    val FOOD_NM_KR: String?,     // 식품명
    val AMT_NUM1: String?,       // 열량
    val AMT_NUM8: String?        // 당류
)

data class I2790(val row: List<FoodItem>?)
data class FoodResponse(
    val body: FoodBody?
)

data class FoodBody(
    val items: List<FoodItem>?
)