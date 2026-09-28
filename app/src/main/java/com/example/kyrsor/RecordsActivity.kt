package com.example.kyrsor

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RecordsActivity : AppCompatActivity() {

    private lateinit var recordsContainer: LinearLayout
    private lateinit var tvNoRecords: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)
        recordsContainer = findViewById(R.id.recordsContainer)
        tvNoRecords = findViewById(R.id.tvNoRecords)
        btnBack = findViewById(R.id.btnBack)
        loadRecords()
        btnBack.setOnClickListener {
            finish()
        }
    }
    private fun loadRecords() {
        val prefs = getSharedPreferences("records", Context.MODE_PRIVATE)

        val ranges = listOf(
            "1-50" to "range_1_50",
            "1-100" to "range_1_100",
            "1-200" to "range_1_200"
        )
        var hasRecords = false
        for ((rangeLabel, key) in ranges) {
            val record = prefs.getInt(key, Int.MAX_VALUE)

            if (record != Int.MAX_VALUE) {
                hasRecords = true

                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(16, 16, 16, 16)
                    setBackgroundResource(R.drawable.history_bg)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 8
                    }
                }
                val tvRange = TextView(this).apply {
                    text = rangeLabel
                    textSize = 16f
                    setTextColor(Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val tvRecord = TextView(this).apply {
                    text = "$record попыток"
                    textSize = 16f
                    setTextColor(Color.parseColor("#FFEB3B"))
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                row.addView(tvRange)
                row.addView(tvRecord)
                recordsContainer.addView(row)
            }
        }
        if (!hasRecords) {
            tvNoRecords.visibility = android.view.View.VISIBLE
        }
    }
}