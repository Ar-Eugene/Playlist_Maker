package com.example.playlist_maker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backArrowplayButton = findViewById<ImageView>(R.id.back_arrow)
        backArrowplayButton.setOnClickListener {
            finish()
        }
    }
}