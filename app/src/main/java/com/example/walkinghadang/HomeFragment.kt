package com.example.walkinghadang

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.walkinghadang.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadBloodSugarChart()
        loadRecentBloodRecord()

        binding.btnGotoBlood.setOnClickListener {
            (activity as? MainActivity)?.apply {
                binding.bottomNavigation.selectedItemId = R.id.nav_blood
                switchFragment(BloodFragment())
            }
        }
    }

    private fun loadRecentBloodRecord() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val showRecent = prefs.getBoolean("show_recent_blood", true)
        if (showRecent) {
            binding.homeRecentBlood.visibility = View.VISIBLE
            MyApplication.db.collection("blood_sugar")
                .whereEqualTo("userEmail", MyApplication.email ?: "")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("timeOrder", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    val latest = result.documents.firstOrNull()?.toObject(BloodSugarData::class.java)
                    binding.homeRecentBlood.text = latest?.let {
                        "최근 혈당: ${it.time} - ${it.level}mg/dL"
                    } ?: "최근 혈당 기록이 없습니다."
                }
                .addOnFailureListener {
                    Log.e("HomeFragment", "Firestore 로딩 실패: ${it.message}")
                    binding.homeRecentBlood.text = "혈당 기록 로딩 실패"
                }
        }else{
            binding.homeRecentBlood.visibility = View.GONE
        }
    }

    private fun loadBloodSugarChart() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val threshold = prefs.getString("blood_sugar_threshold", "140")?.toFloatOrNull() ?: 140f
        val animate = prefs.getBoolean("chart_animation_enabled", true)

        MyApplication.db.collection("blood_sugar")
            .whereEqualTo("userEmail", MyApplication.email ?: "")
            .orderBy("date")
            .orderBy("timeOrder")
            .get()
            .addOnSuccessListener { result ->
                val entries = mutableListOf<Entry>()
                val labels = mutableListOf<String>()

                result.documents.forEachIndexed { index, doc ->
                    val item = doc.toObject(BloodSugarData::class.java)
                    if (item != null) {
                        entries.add(Entry(index.toFloat(), item.level.toFloat()))
                        labels.add(item.time ?: "")
                    }
                }

                val dataSet = LineDataSet(entries, "혈당")
                dataSet.setDrawValues(false)
                dataSet.color = Color.parseColor("#4CAF50") // 선 색상 (녹색)
                dataSet.setCircleColor(Color.parseColor("#FF5722")) // 점 색상 (주황)
                dataSet.circleRadius = 5f
                dataSet.lineWidth = 3f

                // 부드러운 곡선
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

                // 안쪽 색상 채우기
                dataSet.setDrawFilled(true)
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient_fill)
                dataSet.fillDrawable = drawable

                val data = LineData(dataSet)
                val chart = binding.lineChart
                chart.data = data
                chart.description.text = "최근 혈당 변화"
                chart.setTouchEnabled(true)
                chart.setPinchZoom(true)
                chart.setScaleEnabled(true)

                // X축 라벨
                chart.xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    granularity = 1f
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 12f
                }

                // Y축 기준선 (140mg/dL)
                val limitLine = LimitLine(threshold, "기준치")
                limitLine.lineColor = Color.RED
                limitLine.lineWidth = 1.5f
                limitLine.textColor = Color.RED
                limitLine.textSize = 12f
                chart.axisLeft.addLimitLine(limitLine)
                chart.axisLeft.setDrawLimitLinesBehindData(false)

                chart.axisRight.isEnabled = false
                chart.legend.isEnabled = false
                if (animate) {
                    chart.animateX(1000)
                }
                chart.invalidate()
            }
            .addOnFailureListener {
                Log.e("HomeFragment", "Firestore 차트 로딩 실패: ${it.message}")
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}