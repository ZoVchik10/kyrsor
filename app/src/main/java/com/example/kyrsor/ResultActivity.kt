package com.example.kyrsor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var tvSecretNumber: TextView
    private lateinit var tvAttemptsResult: TextView
    private lateinit var tvBestRecord: TextView
    private lateinit var recordLayout: LinearLayout
    private lateinit var btnPlayAgain: Button
    private lateinit var btnRecords: Button
    private lateinit var btnMenu: Button

    private var secretNumber = 0
    private var attempts = 0
    private var minRange = 1
    private var maxRange = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Получаем данные из GameActivity
        secretNumber = intent.getIntExtra("SECRET_NUMBER", 0)
        attempts = intent.getIntExtra("ATTEMPTS", 0)
        minRange = intent.getIntExtra("MIN_RANGE", 1)
        maxRange = intent.getIntExtra("MAX_RANGE", 100)

        initViews()
        displayResults()
        setupListeners()
    }

    private fun initViews() {
        tvSecretNumber = findViewById(R.id.tvSecretNumber)
        tvAttemptsResult = findViewById(R.id.tvAttemptsResult)
        tvBestRecord = findViewById(R.id.tvBestRecord)
        recordLayout = findViewById(R.id.recordLayout)
        btnPlayAgain = findViewById(R.id.btnPlayAgain)
        btnRecords = findViewById(R.id.btnRecords)
        btnMenu = findViewById(R.id.btnMenu)
    }
    private fun displayResults() {
        tvSecretNumber.text = secretNumber.toString()
        tvAttemptsResult.text = attempts.toString()

        // Проверяем рекорд
        val prefs = getSharedPreferences("records", Context.MODE_PRIVATE)
        val key = "range_${minRange}_${maxRange}"
        val best = prefs.getInt(key, Int.MAX_VALUE)

        if (attempts <= best) {
            recordLayout.visibility = LinearLayout.VISIBLE
            tvBestRecord.text = "Лучший результат: $attempts попыток"
        } else {
            recordLayout.visibility = LinearLayout.GONE
        }
    }
    private fun setupListeners() {
        btnPlayAgain.setOnClickListener {
            // Возвращаемся в игру с тем же диапазоном
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("MIN_RANGE", minRange)
                putExtra("MAX_RANGE", maxRange)
            }
            startActivity(intent)
            finish()
        }
        btnRecords.setOnClickListener {
            startActivity(Intent(this, RecordsActivity::class.java))
        }
        btnMenu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity() // Закрываем все активности
        }
    }
}