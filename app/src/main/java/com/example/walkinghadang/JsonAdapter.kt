package com.example.walkinghadang

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.walkinghadang.databinding.ItemGilBinding

class JsonViewHolder(val binding: ItemGilBinding): RecyclerView.ViewHolder(binding.root)
class JsonAdapter(val datas:MutableList<myJsonRow>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return JsonViewHolder(ItemGilBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as JsonViewHolder).binding
        val model = datas!![position]

        binding.GILNM.text = model.GIL_NM
        binding.LVCD.text = model.LV_CD
        binding.REQTM.text = model.REQ_TM
    }
}