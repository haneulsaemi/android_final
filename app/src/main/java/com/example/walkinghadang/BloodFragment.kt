package com.example.walkinghadang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.walkinghadang.databinding.FragmentBloodBinding
import com.google.firebase.firestore.Query

class BloodFragment : Fragment() {

    private var _binding: FragmentBloodBinding? = null
    private val binding get() = _binding!!

    private val TAG = "BloodFragment"
    private lateinit var checkTimes: List<String>
    private var selectedIndex = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBloodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkTimes = listOf("공복", "아침 식전", "아침 식후", "점심 식전", "점심 식후",  "저녁 식전", "저녁 식후", "취침 전", "기타")

        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_bg, checkTimes)
        val autoComplete = binding.checkTimeText.editText as? AutoCompleteTextView
        autoComplete?.setAdapter(adapter)
        autoComplete?.setText(checkTimes[selectedIndex], false)
        autoComplete?.setOnItemClickListener { _, _, position, _ ->
            selectedIndex = position
        }

        loadLatestRecordFromFirestore()

        binding.btnSave.setOnClickListener {
            val input = binding.bgLevelEditText.text.toString().trim()
            if (input.isBlank()) {
                Toast.makeText(requireContext(), "혈당 수치를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val level = input.toIntOrNull()
            if (level == null || level <= 0) {
                Toast.makeText(requireContext(), "유효한 혈당 수치를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val time = checkTimes[selectedIndex]

            val timeOrder = selectedIndex
            val data = BloodSugarData(
                level = level,
                date = getTodayDate(),
                time = time,
                timeOrder = timeOrder,
                memo = "",
                userEmail = MyApplication.email ?: ""
            )
            saveBloodSugarToFirestore(data)
            binding.bgLevelEditText.text?.clear()
            autoComplete?.setText(checkTimes[1], false)
        }
    }

    private fun saveBloodSugarToFirestore(data: BloodSugarData) {
        MyApplication.db.collection("blood_sugar")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "저장 완료", Toast.LENGTH_SHORT).show()
                binding.lastRecordText.text = "최근 기록: ${data.time} - ${data.level} mg/dL"
            }
            .addOnFailureListener {
                Log.e(TAG, "Firestore 저장 실패: ${it.message}")
                Toast.makeText(requireContext(), "저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadLatestRecordFromFirestore() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val showRecent = prefs.getBoolean("show_recent_blood", true)
        if (showRecent) {
            binding.lastRecordText.visibility = View.VISIBLE
            MyApplication.db.collection("blood_sugar")
                .whereEqualTo("userEmail", MyApplication.email ?: "")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("timeOrder", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    val latest =
                        result.documents.firstOrNull()?.toObject(BloodSugarData::class.java)
                    binding.lastRecordText.text = latest?.let {
                        "최근 기록: ${it.time} - ${it.level} mg/dL"
                    } ?: "최근 기록 없음"
                }
                .addOnFailureListener {
                    Log.e(TAG, "Firestore 로딩 실패: ${it.message}")
                    Toast.makeText(requireContext(), "불러오기 실패: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }else{
            binding.lastRecordText.visibility = View.GONE
        }
    }

    private fun getTodayDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
