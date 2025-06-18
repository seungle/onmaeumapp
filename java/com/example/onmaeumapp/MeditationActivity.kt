package com.example.onmaeumapp

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onmaeumapp.R

class MeditationActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        setupMediaPlayer()
        setupUI()
    }

    private fun setupMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.meditation_sound)?.apply {
                setOnCompletionListener {
                    isPlaying = false
                    updatePlayButton()
                }
                setOnErrorListener { _, what, extra ->
                    Toast.makeText(
                        this@MeditationActivity,
                        getString(R.string.error_playing_meditation),
                        Toast.LENGTH_SHORT
                    ).show()
                    isPlaying = false
                    updatePlayButton()
                    true
                }
            } ?: throw IllegalStateException("Failed to create MediaPlayer")
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.error_initializing_media),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun setupUI() {
        val playButton = findViewById<Button>(R.id.playMeditation)
        playButton.setOnClickListener {
            togglePlayback()
        }
    }

    private fun togglePlayback() {
        mediaPlayer?.let { player ->
            if (isPlaying) {
                player.pause()
            } else {
                try {
                    player.start()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        getString(R.string.error_playing_meditation),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
            isPlaying = !isPlaying
            updatePlayButton()
        }
    }

    private fun updatePlayButton() {
        findViewById<Button>(R.id.playMeditation).text = getString(
            if (isPlaying) R.string.pause_meditation else R.string.play_meditation
        )
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        isPlaying = false
        updatePlayButton()
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer == null) {
            setupMediaPlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}
