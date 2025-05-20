package com.example.walkinghadang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkinghadang.databinding.ActivityMainBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MainActivity : AppCompatActivity() {
    val TAG = "25android"

    private fun loadBloodSugarData() {
        val db = DBHelper(this).readableDatabase
        val datas = mutableListOf<Int>()
        val cursor = db.rawQuery("select * from BLOODGLU_TB", null)
        cursor.use {
            while (it.moveToNext()) {
                datas.add(it.getInt(2))
            }
        }
        db.close()

        val entries = datas.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }
        val dataSet = LineDataSet(entries, "혈당 수치").apply {
            color = Color.RED
            setCircleColor(Color.RED)
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
        }

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate() // 차트 갱신
    }

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        binding.toolbar.setTitle("워킹하당")
        binding.toolbar.setTitleTextColor(Color.parseColor("#ffffff"))

        loadBloodSugarData()

        binding.btnCheckBG.setOnClickListener {
            val intent = Intent(it.context, CheckBgActivity::class.java)
            startActivity(intent)
        }

        binding.btnGil.setOnClickListener {
            val intent = Intent(it.context, GilViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadBloodSugarData()

    }
}