package com.example.playlist_maker.main.ui

import MainViewModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlist_maker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
//        mainViewModel.applyTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        // Наблюдаем за изменениями темы
//        mainViewModel.isDarkThemeLiveData.observe(this) { isDarkTheme ->
//            // Применяем тему при изменении
//            mainViewModel.applyTheme()
//        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)


    }
}