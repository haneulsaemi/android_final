package com.example.walkinghadang

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkinghadang.databinding.ActivityGilViewBinding

class GilViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityGilViewBinding

    private fun loadJsonFragment(keyword : String, searchType: String){
        val jsonfragment = JsonFragment()
        val bundle = Bundle()

        when(searchType){
            "GIL_NM" -> bundle.putString("gilNm", keyword)
            "LV_CD" -> bundle.putString("lvCd", keyword)
            "REQ_TM" -> bundle.putString("reqTm", keyword)
        }

        jsonfragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_content, jsonfragment)
            .commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGilViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadJsonFragment("", "GIL_NM")
        binding.btnSearch.setOnClickListener {
            val keyword = binding.edtLoc.text.toString()
            if (keyword.isBlank()) {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedId = binding.rGroup.checkedRadioButtonId
            val searchType = when (selectedId) {
                R.id.rbGil -> "GIL_NM"
                R.id.rbLvl -> "LV_CD"
                R.id.rbTime -> "REQ_TM"
                else -> "GIL_NM"
            }
                loadJsonFragment(keyword, searchType)
            }
        }
}