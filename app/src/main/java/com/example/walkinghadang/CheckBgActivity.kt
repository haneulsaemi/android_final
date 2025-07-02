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

    private fun getLatestRecord(): String? {
        val db = DBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT time, level FROM BLOODGLU_TB ORDER BY _id DESC LIMIT 1", null)

        var result: String? = null
        if (cursor.moveToFirst()) {
            val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))
            val level = cursor.getInt(cursor.getColumnIndexOrThrow("level"))
            result = "최근 기록: $time - ${level} mg/dL"
        }

        cursor.close()
        db.close()
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCheckBgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val checkTimeTextView = binding.checkTimeText;
        val checkTimes = listOf("공복", "아침 식전", "아침 식후", "점심 식전", "점심 식후", "저녁 식전", "저녁 식후", "취침 전", "기타")
        val adapter = ArrayAdapter(this,R.layout.list_item_bg, checkTimes)
        val autoComplete = (checkTimeTextView.editText as? AutoCompleteTextView)
        var selectedIndex = 1;

        val lastRecordTextView = binding.lastRecordText  // xml에 ID가 있어야 함
        val latest = getLatestRecord()
        if (latest != null) {
            lastRecordTextView.text = latest
        } else {
            lastRecordTextView.text = "최근 기록 없음"
        }

        autoComplete?.setText(checkTimes[selectedIndex], false)
        autoComplete?.setAdapter(adapter)

        autoComplete?.setOnItemClickListener { parent, view, position, id ->
            selectedIndex = position
        }
        var bg_level = -1;
        binding.btnSave.setOnClickListener {
            val input = binding.bgLevelEditText.text.toString().trim()

            if (input.isBlank()) {
                Toast.makeText(this, "혈당 수치를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val level = input.toIntOrNull()
            if (level == null || level <= 0) {
                Toast.makeText(this, "유효한 혈당 수치를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val time = checkTimes[selectedIndex]

            try {
                val db = DBHelper(this).writableDatabase
                db.execSQL("INSERT INTO BLOODGLU_TB (time, level) VALUES (?, ?)", arrayOf(time, level))
                db.close()

                // 입력창 초기화
                binding.bgLevelEditText.text?.clear()
                autoComplete?.setText(checkTimes[1], false)

                Toast.makeText(this, "혈당이 저장되었습니다", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "DB Insert Error: ${e.message}")
                Toast.makeText(this, "저장 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}