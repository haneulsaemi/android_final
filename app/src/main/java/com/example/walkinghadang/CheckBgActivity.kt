package com.example.walkinghadang

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkinghadang.databinding.ActivityCheckBgBinding

class CheckBgActivity : AppCompatActivity() {
    val TAG = "25android"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCheckBgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val checkTimeTextView = binding.checkTimeText;
        val checkTimes = listOf("공복", "아침 식전", "아침 식후", "점심 식후", "점심 식전", "저녁 식전", "저녁 식후", "취침 전", "기타")
        val adapter = ArrayAdapter(this,R.layout.list_item_bg, checkTimes)
        val autoComplete = (checkTimeTextView.editText as? AutoCompleteTextView)
        var selectedIndex = 1;
        autoComplete?.setText(checkTimes[selectedIndex], false)
        autoComplete?.setAdapter(adapter)

        autoComplete?.setOnItemClickListener { parent, view, position, id ->
            selectedIndex = position
        }
        var bg_level = -1; 
        binding.btnSave.setOnClickListener {
            val input = binding.bgLevelEditText.text.toString()
            if(input.isBlank()) {
                Toast.makeText(this, "혈당 수치를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val time = checkTimes.get(selectedIndex)
            if(time == null){
                Toast.makeText(this, "시간 선택 오류", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try{
                bg_level = input.toInt()
                val db = DBHelper(this).writableDatabase
                db.execSQL("insert into BLOODGLU_TB (time, level) values (?, ?)", arrayOf(checkTimes[selectedIndex], bg_level ))
                finish()

            }catch (e: Exception){
                e.printStackTrace()
                Log.d(TAG, "$e.message")
            }
            Log.d(TAG, "입력된 혈당 수치: $bg_level, 시간: $time")
        }
    }
}