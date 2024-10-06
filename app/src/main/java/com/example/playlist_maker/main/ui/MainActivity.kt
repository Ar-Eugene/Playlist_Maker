package com.example.playlist_maker.main.ui

import MainViewModel
import MainViewModelFactory
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.R
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.mediateca.MediatecaActivity
import com.example.playlist_maker.search.ui.SearchActivity
import com.example.playlist_maker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val themeInteractor = Creator.provideThemeInteractor()
        val factory = MainViewModelFactory(themeInteractor)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)


        // Применяем тему при создании активити
        viewModel.applyTheme()


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