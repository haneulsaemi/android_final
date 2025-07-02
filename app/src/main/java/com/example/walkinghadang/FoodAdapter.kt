package com.example.walkinghadang

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.walkinghadang.databinding.ItemFoodBinding

class FoodAdapter(private val datas: List<FoodItem>) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    inner class FoodViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FoodViewHolder(ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = datas[position]
        holder.binding.tvProductName.text = item.FOOD_NM_KR ?: "이름 없음"
        holder.binding.tvKcal.text = "열량: ${item.AMT_NUM1?: "?"} kcal"
        holder.binding.tvSugar.text = "당류: ${item.AMT_NUM8?: "?"} g"
    }

    override fun getItemCount() = datas.size
}