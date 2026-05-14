package com.example.lr3mobile_jrnlems

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {

    private lateinit var seekBarMood: SeekBar
    private lateinit var tvMoodValue: TextView
    private lateinit var btnSave: Button
    private lateinit var btnClearAll: Button
    private lateinit var tvHistoryList: TextView
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seekBarMood = view.findViewById(R.id.seekBarMood)
        tvMoodValue = view.findViewById(R.id.tvMoodValue)
        btnSave = view.findViewById(R.id.btnSave)
        btnClearAll = view.findViewById(R.id.btnClearAll)
        tvHistoryList = view.findViewById(R.id.tvHistoryList)

        prefs = requireActivity().getSharedPreferences("mood_diary", Context.MODE_PRIVATE)
        updateHistoryDisplay()

        // Обработчик изменения ползунка
        seekBarMood.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val moodValue = progress + 1 // Преобразуем 0-9 в 1-10
                val moodEmoji = when (moodValue) {
                    1 -> "😭"
                    2 -> "😭"
                    3 -> "😟"
                    4 -> "😟"
                    5 -> "😐"
                    6 -> "🙂"
                    7 -> "🙂"
                    8 -> "😊"
                    9 -> "😆"
                    10 -> "😆"
                    else -> "😐"
                }
                tvMoodValue.text = "Оценка: $moodValue $moodEmoji"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Устанавливаем начальное значение (5)
        seekBarMood.progress = 5
        tvMoodValue.text = "Оценка: 5 😐"

        btnSave.setOnClickListener {
            val moodValue = seekBarMood.progress + 1
            val moodEmoji = when (moodValue) {
                1 -> "😭"
                2 -> "😭"
                3 -> "😟"
                4 -> "😟"
                5 -> "😐"
                6 -> "🙂"
                7 -> "🙂"
                8 -> "😊"
                9 -> "😆"
                10 -> "😆"
                else -> "😐"
            }
            val moodText = "$moodEmoji $moodValue/10"

            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val dateTime = dateFormat.format(Date())
            val entry = "$dateTime - $moodText\n"

            val existingHistory = prefs.getString("history", "") ?: ""
            prefs.edit().putString("history", entry + existingHistory).apply()

            updateHistoryDisplay()
            // Не сбрасываем ползунок, чтобы можно было записать несколько похожих настроений подряд
        }

        btnClearAll.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Очистить журнал")
                .setMessage("Ты уверен? Все записи настроений будут удалены без возможности восстановления.")
                .setPositiveButton("Очистить") { _, _ ->
                    prefs.edit().remove("history").apply()
                    updateHistoryDisplay()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun updateHistoryDisplay() {
        val history = prefs.getString("history", "") ?: ""
        tvHistoryList.text = if (history.isEmpty()) "Пока ничего не записано" else history
    }
}