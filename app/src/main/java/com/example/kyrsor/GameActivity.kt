package com.example.kyrsor

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var tvRange: TextView
    private lateinit var tvEmoji: TextView
    private lateinit var tvHint: TextView
    private lateinit var tvAttempts: TextView
    private lateinit var etNumber: EditText
    private lateinit var btnCheck: Button
    private lateinit var btnMenu: Button
    private lateinit var thermometer: ThermometerView
    private lateinit var historyContainer: LinearLayout

    private var secretNumber = 0
    private var minRange = 1
    private var maxRange = 100
    private var attempts = 0
    private var lastDistance = Int.MAX_VALUE
    private val history = mutableListOf<String>()
    private var isGameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initViews()

        if (savedInstanceState != null) {
            // Восстанавливаем состояние после поворота
            restoreState(savedInstanceState)
        } else {
            // Новая игра
            minRange = intent.getIntExtra("MIN_RANGE", 1)
            maxRange = intent.getIntExtra("MAX_RANGE", 100)
            secretNumber = (minRange..maxRange).random()
        }

        setupListeners()
        updateUI()
    }

    private fun initViews() {
        tvRange = findViewById(R.id.tvRange)
        tvEmoji = findViewById(R.id.tvEmoji)
        tvHint = findViewById(R.id.tvHint)
        tvAttempts = findViewById(R.id.tvAttempts)
        etNumber = findViewById(R.id.etNumber)
        btnCheck = findViewById(R.id.btnCheck)
        btnMenu = findViewById(R.id.btnMenu)
        thermometer = findViewById(R.id.thermometer)
        historyContainer = findViewById(R.id.historyContainer)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        secretNumber = savedInstanceState.getInt("SECRET_NUMBER")
        minRange = savedInstanceState.getInt("MIN_RANGE")
        maxRange = savedInstanceState.getInt("MAX_RANGE")
        attempts = savedInstanceState.getInt("ATTEMPTS")
        lastDistance = savedInstanceState.getInt("LAST_DISTANCE", Int.MAX_VALUE)
        isGameOver = savedInstanceState.getBoolean("IS_GAME_OVER")
        history.clear()
        history.addAll(savedInstanceState.getStringArrayList("HISTORY") ?: emptyList())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SECRET_NUMBER", secretNumber)
        outState.putInt("MIN_RANGE", minRange)
        outState.putInt("MAX_RANGE", maxRange)
        outState.putInt("ATTEMPTS", attempts)
        outState.putInt("LAST_DISTANCE", lastDistance)
        outState.putBoolean("IS_GAME_OVER", isGameOver)
        outState.putStringArrayList("HISTORY", ArrayList(history))
    }

    private fun setupListeners() {
        btnCheck.setOnClickListener {
            if (isGameOver) return@setOnClickListener

            val input = etNumber.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Введите число!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val guess = input.toInt()
            if (guess < minRange || guess > maxRange) {
                Toast.makeText(this, "Число вне диапазона!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            attempts++
            checkNumber(guess)
            etNumber.text.clear()
        }

        btnMenu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkNumber(guess: Int) {
        val distance = Math.abs(guess - secretNumber)
        val rangeSize = maxRange - minRange
        val percent = (distance.toFloat() / rangeSize * 100).toInt().coerceIn(0, 100)

        // Обновляем термометр (чем ближе — тем больше прогресс)
        thermometer.setProgress(100f - percent)

        when {
            distance == 0 -> {
                tvEmoji.text = "😎"
                tvHint.text = "🎉 Угадано за $attempts попыток!"
                tvHint.setTextColor(Color.parseColor("#4CAF50"))
                animateEmoji()
                saveRecord()
                isGameOver = true
                btnCheck.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, ResultActivity::class.java).apply {
                        putExtra("SECRET_NUMBER", secretNumber)
                        putExtra("ATTEMPTS", attempts)
                        putExtra("MIN_RANGE", minRange)
                        putExtra("MAX_RANGE", maxRange)
                    }
                    startActivity(intent)
                    finish()
                }, 1500)
            }
            distance <= rangeSize * 0.1 -> {
                tvEmoji.text = "🔥"
                tvHint.text = "Горячо! Очень близко!"
                tvHint.setTextColor(Color.parseColor("#F44336"))
            }
            distance <= rangeSize * 0.3 -> {
                tvEmoji.text = "🙂"
                tvHint.text = "Тепло... Ты на верном пути"
                tvHint.setTextColor(Color.parseColor("#FFEB3B"))
            }
            else -> {
                tvEmoji.text = ""
                tvHint.text = "Холодно! Очень далеко"
                tvHint.setTextColor(Color.parseColor("#2196F3"))
            }
        }

        val direction = when {
            guess < secretNumber -> "↑ больше"
            guess > secretNumber -> "↓ меньше"
            else -> ""
        }

        val entry = "Попытка $attempts: $guess $direction"
        history.add(0, entry)
        updateHistory()

        animateEmoji()

        if (lastDistance != Int.MAX_VALUE) {
            val warmerOrColder = if (distance < lastDistance) " Теплее!" else "📉 Холоднее!"
            Toast.makeText(this, warmerOrColder, Toast.LENGTH_SHORT).show()
        }
        lastDistance = distance
    }

    private fun animateEmoji() {
        val animator = ObjectAnimator.ofFloat(tvEmoji, "scaleX", 1f, 1.3f, 1f)
        animator.duration = 400
        animator.start()
        val animatorY = ObjectAnimator.ofFloat(tvEmoji, "scaleY", 1f, 1.3f, 1f)
        animatorY.duration = 400
        animatorY.start()
    }

    private fun updateHistory() {
        historyContainer.removeAllViews()
        for (item in history) {
            val tv = TextView(this).apply {
                text = item
                textSize = 14f
                setTextColor(Color.WHITE)
                gravity = Gravity.START
                setPadding(8, 4, 8, 4)
            }
            historyContainer.addView(tv)
        }
    }

    private fun updateUI() {
        tvRange.text = "Диапазон: от $minRange до $maxRange"
        tvAttempts.text = "Попыток: $attempts"
        updateHistory()
    }

    private fun saveRecord() {
        val prefs = getSharedPreferences("records", Context.MODE_PRIVATE)
        val key = "range_${minRange}_${maxRange}"
        val best = prefs.getInt(key, Int.MAX_VALUE)
        if (attempts < best) {
            prefs.edit().putInt(key, attempts).apply()
            Toast.makeText(this, "🏆 Новый рекорд: $attempts попыток!", Toast.LENGTH_LONG).show()
        }
    }
}