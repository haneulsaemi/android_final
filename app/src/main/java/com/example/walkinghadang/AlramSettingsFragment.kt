package com.example.walkinghadang

import com.example.walkinghadang.AlarmReceiver
import com.example.walkinghadang.DBHelper
import com.example.walkinghadang.databinding.FragmentAlramSettingsBinding
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment

import java.util.*

class AlarmSettingsFragment : Fragment() {
    private var _binding: FragmentAlramSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlramSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSavedAlarms()

        binding.btnAddAlarm.setOnClickListener {
            addTimePickerView()
        }

        binding.btnSaveAlarms.setOnClickListener {
            saveAllAlarms()
        }
    }

    private fun loadSavedAlarms() {
        val db = DBHelper(requireContext()).readableDatabase
        val cursor = db.rawQuery("SELECT _id, hour, minute FROM ALARM_TB", null)
        binding.alarmContainer.removeAllViews()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val hour = cursor.getInt(cursor.getColumnIndexOrThrow("hour"))
            val minute = cursor.getInt(cursor.getColumnIndexOrThrow("minute"))
            val timeStr = String.format("%02d:%02d", hour, minute)

            val button = Button(requireContext()).apply {
                text = "$timeStr 알림 삭제"
                setOnClickListener {
                    deleteAlarm(id)
                    binding.alarmContainer.removeView(this)
                    cancelAlarm(id)
                    Toast.makeText(requireContext(), "$timeStr 알림이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                }
            }

            binding.alarmContainer.addView(button)
        }

        cursor.close()
        db.close()
    }

    private fun addTimePickerView() {
        val timePicker = TimePicker(requireContext()).apply {
            setIs24HourView(true)
        }
        binding.alarmContainer.addView(timePicker)
    }

    private fun saveAllAlarms() {
        val db = DBHelper(requireContext()).writableDatabase
        var count = 0

        for (i in 0 until binding.alarmContainer.childCount) {
            val view = binding.alarmContainer.getChildAt(i)
            if (view is TimePicker) {
                val hour = view.hour
                val minute = view.minute

                // 🔍 이미 존재하는 알람인지 검사
                val cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM ALARM_TB WHERE hour = ? AND minute = ?",
                    arrayOf(hour.toString(), minute.toString())
                )
                cursor.moveToFirst()
                val exists = cursor.getInt(0) > 0
                cursor.close()

                if (!exists) {
                    db.execSQL("INSERT INTO ALARM_TB (hour, minute) VALUES (?, ?)", arrayOf(hour, minute))
                    setDailyAlarm(generateRequestCode(hour, minute), hour, minute)
                    count++
                }
            }
        }

        db.close()

        // 🔁 UI 리프레시
        binding.alarmContainer.removeAllViews()
        loadSavedAlarms()

        Toast.makeText(requireContext(), "새 알람 ${count}개 저장 완료", Toast.LENGTH_SHORT).show()
    }
    private fun generateRequestCode(hour: Int, minute: Int): Int {
        return hour * 100 + minute  // 예: 08:30 → 830
    }

    private fun setDailyAlarm(requestCode: Int, hour: Int, minute: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Log.d("AlarmSettings", "알람 설정됨: $hour:$minute (ID: $requestCode)")
    }

    private fun deleteAlarm(id: Int) {
        val db = DBHelper(requireContext()).writableDatabase
        db.execSQL("DELETE FROM ALARM_TB WHERE _id = ?", arrayOf(id))
        db.close()
    }

    private fun cancelAlarm(requestCode: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
