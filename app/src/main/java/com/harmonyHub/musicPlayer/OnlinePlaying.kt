package com.harmonyHub.musicPlayer

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.squareup.picasso.Picasso
import java.util.*

class OnlinePlaying : AppCompatActivity() {

    private lateinit var thumbnail: MutableList<String>
    private lateinit var lyric: String
    private var duration: Int = -1
    private lateinit var jcPlayerView1: JcPlayerView
    private lateinit var jcAudios: List<JcAudio>
    private var currentSongId: Int = -1
    private var currentLyricIndex: Int = 0
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setContentView(R.layout.activity_online_playing)
        supportActionBar?.hide()
        jcPlayerView1 = findViewById(R.id.jcplayer1)
        val songId = intent.getIntExtra("songId", -1)
        thumbnail = intent.getStringArrayListExtra("thumbnail") as MutableList<String>
        jcAudios = intent.getParcelableArrayListExtra<JcAudio>("jcAudios") ?: emptyList()
        lyric = intent.getStringExtra("lyric") ?: ""
        duration = convertDurationToSeconds(intent.getStringExtra("duration"))

        if (songId != -1) {
            initializeJcPlayerViews(jcAudios, songId, thumbnail)
            startLyricScrolling()
        } else {
            Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLyricScrolling()
    }

    override fun onResume() {
        super.onResume()
        if (jcPlayerView1.currentAudio?.position != currentSongId) {
            currentSongId = jcPlayerView1.currentAudio?.position ?: -1
            updateThumbnail(jcPlayerView1.currentAudio)
        }
    }

    private fun initializeJcPlayerViews(jcAudios: List<JcAudio>, id: Int, thumbnail: List<String>) {
        jcPlayerView1.initPlaylist(jcAudios, null)
        if (id >= 0 && id < jcAudios.size) {
            currentSongId = id
            jcPlayerView1.playAudio(jcAudios[id])
            jcPlayerView1.createNotification(R.drawable.notimg)
            updateThumbnail(jcAudios[id])
        }
    }

    private fun startLyricScrolling() {
        val lyricTextView: TextView = findViewById(R.id.lyricTextView)
        val lyrics = processLyrics(lyric)
        currentLyricIndex = 0

        if (lyrics.isNotEmpty()) {
            val lyricScrollDuration = duration * 1000 / lyrics.size

            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (currentLyricIndex < lyrics.size) {
                            lyricTextView.text = lyrics[currentLyricIndex]
                            currentLyricIndex++
                        } else {
                            stopLyricScrolling()
                        }
                    }
                }
            }, 0, lyricScrollDuration.toLong())
        }
    }

    private fun stopLyricScrolling() {
        timer?.cancel()
        timer = null
    }

    private fun updateThumbnail(jcAudio: JcAudio?) {
        jcAudio?.let {
            val position = jcAudios.indexOf(it)
            if (position != -1 && position < thumbnail.size) {
                val imageView2: ImageView = findViewById(R.id.imageView2)
                Picasso.get().load(thumbnail[position]).into(imageView2)
            }
        }
    }

    private fun processLyrics(lyric: String): List<String> {
        val lyricsList = mutableListOf<String>()
        if (lyric.contains("\\n")) {
            val lyricsArray = lyric.split("\\n")
            lyricsList.addAll(lyricsArray)
        } else {
            lyricsList.add(lyric)
        }
        return lyricsList
    }

    private fun convertDurationToSeconds(durationString: String?): Int {
        if (durationString == null) {
            return -1
        }
        val parts = durationString.split(":")
        if (parts.size != 2) {
            return -1
        }
        val minutes = parts[0].toIntOrNull()
        val seconds = parts[1].toIntOrNull()
        if (minutes == null || seconds == null) {
            return -1
        }
        return minutes * 60 + seconds
    }
}