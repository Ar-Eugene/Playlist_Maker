package com.example.playlist_maker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlist_maker.search.data.network.NetworkClient
import com.example.playlist_maker.search.data.network.RetrofitNetworkClient
import com.example.playlist_maker.search.data.network.TrackApi
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single { Gson() }

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(trackApi = get())
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    single {
        androidContext().getSharedPreferences("MY_TRACKS", Context.MODE_PRIVATE)
    }

}