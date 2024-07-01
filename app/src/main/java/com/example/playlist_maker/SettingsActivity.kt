package com.example.playlist_maker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backArrowplayButton = findViewById<ImageView>(R.id.back_arrow)
        backArrowplayButton.setOnClickListener {
            finish()
        }
        val shareAppImageView = findViewById<LinearLayout>(R.id.share)
        shareAppImageView.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = getString(R.string.text_plain)
                putExtra(Intent.EXTRA_TEXT, getString(R.string.URL_Android_developer))
            }, getString(R.string.share_app)))
        }
        val writeToSupportImageView = findViewById<LinearLayout>(R.id.write_to_support)
        writeToSupportImageView.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mailto))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.letter_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.thanksToTheDevelopers))
            }
                startActivity(emailIntent)
        }
        val agreementImageView = findViewById<LinearLayout>(R.id.agreementLayout)
        agreementImageView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URL_agreement)))
            startActivity(browserIntent)
        }
    }
}