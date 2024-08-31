package com.example.playlist_maker.constans

class Constants {
    companion object{
        // для хранения темы
        const val SHARED_PREFERERNCES = "data_preferences"
        // ключ для хранения темы
        const val SEARCH_KEY = "key_for_shared"
        // для json преобразования
        const val TRACKLIST_HISTORY = "TRACKLIST_HISTORY"
        // для хранения истории треков
        const val HISTORY_TRACKLIST = "MY_TRACKS"
        // для хранения времения отклика
         const val SEARCH_DEBOUNCE_DELAY = 2000L
        // для хранения задержки отклика от пользователя
        const val CLICK_DEBOUNCE_DELAY = 1000L
        // кажется это для хранения передачи данных между поиском и медиаплеером
        const val REQUEST_CODE_PLAYER = 1
        // константы для отслеживания состояние медиаплеера
         const val STATE_DEFAULT = 0
         const val STATE_PREPARED = 1
         const val STATE_PLAYING = 2
         const val STATE_PAUSED = 3
        //
    }
}