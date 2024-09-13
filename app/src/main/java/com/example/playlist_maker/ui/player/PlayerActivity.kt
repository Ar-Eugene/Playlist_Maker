package com.example.playlist_maker.ui.player


import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.constans.Constants
import com.example.playlist_maker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var playerState = Constants.STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val mainHandler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            //проверяет, воспроизводится ли в данный момент трек. Обновление времени происходит только тогда, когда трек играет.
            if (playerState == Constants.STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition //получает текущую позицию воспроизведения трека.
                //преобразует текущую позицию из миллисекунд в строку формата "минуты:секунды" и
                // обновляет TextView, чтобы отобразить текущее время воспроизведения.
                binding.textTrackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                mainHandler.postDelayed(this, 1000) // Обновление каждую секунду
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playButton.setOnClickListener {
            playbackControl()
        }

        // Получаем переданные данные
        val previewUrl = intent.getStringExtra("previewUrl") ?: return
        val trackName = intent.getStringExtra("trackName")
        val artistName = intent.getStringExtra("artistName")
        val trackTimeMillis = intent.getLongExtra("trackTimeMillis", 0)
        val artworkUrl100 = intent.getStringExtra("artworkUrl100")
        val country = intent.getStringExtra("country")
        val collectionName = intent.getStringExtra("collectionName")
        val primaryGenreName = intent.getStringExtra("primaryGenreName")
        val releaseDate = intent.getStringExtra("releaseDate")

        // Установка значений на экран
        binding.trackName.text = trackName
        binding.trackArtist.text = artistName
        binding.textDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        val artworkUrl512 = artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.error_image)
            .error(R.drawable.error_image)
            .transform(RoundedCorners(20))
            .into(binding.albumPosterImage)
        binding.textCountryValue.text = country
        binding.textGenreValue.text = primaryGenreName
        binding.textYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(releaseDate)!!)
        binding.textAlbumValue.text = collectionName

        preparePlayer(previewUrl)
        // Возвращаемся на экран "Поиск"
        backarrowButton()
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
    //отвечает за подготовку MediaPlayer для воспроизведения аудиотрека.
    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)//устанавливает URL-адрес аудиотрека, который нужно воспроизвести.
        mediaPlayer.prepareAsync()//начинает асинхронную подготовку MediaPlayer, чтобы избежать блокировки основного потока.
        mediaPlayer.setOnPreparedListener {
            binding.playButton.isEnabled = true// включает кнопку воспроизведения, делая её активной для пользователя.
            playerState = Constants.STATE_PREPARED//обновляет состояние плеера, указывая, что он готов к воспроизведению.
        }
        mediaPlayer.setOnCompletionListener {// устанавливает слушателя, который срабатывает, когда воспроизведение трека завершено.
            playerState = Constants.STATE_PREPARED//сбрасывает состояние плеера.
            mainHandler.removeCallbacks(updateTimeRunnable)//останавливает обновление времени воспроизведения.
            binding.textTrackTimeValue.text = getString(R.string.current_time)
            binding.playButton.setImageResource(R.drawable.play_button)
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.pause_button)
        playerState = Constants.STATE_PLAYING
        mainHandler.post(updateTimeRunnable)
    }
    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.play_button)
        playerState = Constants.STATE_PAUSED
        mainHandler.removeCallbacks(updateTimeRunnable)
    }
    // метод отвечает за управление воспроизведением аудиотрека в зависимости от текущего состояния плеера.
    private fun playbackControl() {
        when (playerState) {
            Constants.STATE_PLAYING -> {
                pausePlayer()
            }
            Constants.STATE_PREPARED, Constants.STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    override fun onBackPressed() {
        val isFromHistory = intent.getBooleanExtra("isFromHistory", false)
        val resultIntent = Intent().apply {
            putExtra("isFromHistory", isFromHistory)
        }
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }
    fun backarrowButton() {
        val backArrowplayButton = binding.backArrow
        backArrowplayButton.setOnClickListener {
            val isFromHistory = intent.getBooleanExtra("isFromHistory", false)
            val resultIntent = Intent().apply {
                putExtra("isFromHistory", isFromHistory)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}