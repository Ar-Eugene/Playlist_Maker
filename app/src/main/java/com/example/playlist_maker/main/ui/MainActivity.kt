package com.example.playlist_maker.main.ui

import MainViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.playlist_maker.R
import com.example.playlist_maker.mediateca.MediatecaActivity
import com.example.playlist_maker.search.ui.SearchActivity
import com.example.playlist_maker.settings.ui.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Применяем тему при создании активити
        mainViewModel.applyTheme()


        val searchPlayButton = findViewById<Button>(R.id.search)
        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        searchPlayButton.setOnClickListener(searchButtonClickListener)

        val mediatecaPlayButton = findViewById<Button>(R.id.mediateca)
        mediatecaPlayButton.setOnClickListener {
            val mediatecaPlayIntent = Intent(this, MediatecaActivity::class.java)
            startActivity(mediatecaPlayIntent)
        }
        val settingsPlayButton = findViewById<Button>(R.id.settings)
        settingsPlayButton.setOnClickListener {
            val settingsPlayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsPlayIntent)
        }

    }

}