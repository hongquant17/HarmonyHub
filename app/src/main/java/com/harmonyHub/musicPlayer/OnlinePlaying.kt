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

class OnlinePlaying : AppCompatActivity() {

    private lateinit var thumbnail: MutableList<String>
    private lateinit var lyric: String
    private lateinit var jcPlayerView1: JcPlayerView
    private lateinit var jcAudios: List<JcAudio>
    private var currentSongId: Int = -1

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

        if (songId != -1) {
            initializeJcPlayerViews(jcAudios, songId, thumbnail)
            showLyrics(lyric)
        } else {
            Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
        }
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

    private fun showLyrics(lyric: String?) {
        val lyricTextView: TextView = findViewById(R.id.lyricTextView)
        lyricTextView.text = lyric ?: ""
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
}