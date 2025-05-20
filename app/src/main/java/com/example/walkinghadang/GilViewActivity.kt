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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGilViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var jsonfragment = JsonFragment()
        var xmlfragment = XmlFragment()
        val bundle = Bundle()

        binding.btnSearch.setOnClickListener {
            val loc = binding.edtLoc.text.toString()
            if(loc == ""){
                Toast.makeText(this, "길이름을 입력하세요. ", Toast.LENGTH_SHORT).show()
            }
            else {
                jsonfragment = JsonFragment()
                bundle.putString("gilNm", binding.edtLoc.text.toString())


                jsonfragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_content, jsonfragment)
                    .commit()

            }
        }
    }
}