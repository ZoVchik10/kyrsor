package com.example.kyrsor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnRecords = findViewById<Button>(R.id.btnRecords)

        btnStart.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val (min, max) = when (selectedId) {
                R.id.radio50 -> 1 to 50
                R.id.radio200 -> 1 to 200
                else -> 1 to 100
            }

            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("MIN_RANGE", min)
                putExtra("MAX_RANGE", max)
            }
            startActivity(intent)
        }

        btnRecords.setOnClickListener {
            startActivity(Intent(this, RecordsActivity::class.java))
        }
    }
}