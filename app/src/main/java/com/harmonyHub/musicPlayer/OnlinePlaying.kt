package com.harmonyHub.musicPlayer

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.squareup.picasso.Picasso

class OnlinePlaying : AppCompatActivity() {

    private lateinit var thumbnail: MutableList<String>
    private lateinit var jcPlayerView1: JcPlayerView
//    private lateinit var jcPlayerView2: JcPlayerView
    private lateinit var jcAudios: List<JcAudio>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_playing)
        supportActionBar?.hide() // For Hiding The Action bar

        jcPlayerView1 = findViewById(R.id.jcplayer1)
//        jcPlayerView2 = findViewById(R.id.jcplayer2)
        val songId = intent.getIntExtra("songId", -1)
        thumbnail = intent.getStringArrayListExtra("thumbnail") as MutableList<String>
        jcAudios = intent.getParcelableArrayListExtra<JcAudio>("jcAudios") ?: emptyList()

        if (songId != -1) {
            initializeJcPlayerViews(jcAudios, songId, thumbnail)
        } else {
            Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeJcPlayerViews(jcAudios: List<JcAudio>, id: Int, thumbnail: List<String>) {
        jcPlayerView1.initPlaylist(jcAudios)
//        jcPlayerView2.initPlaylist(jcAudios)

        if (id >= 0 && id < jcAudios.size) {
            jcPlayerView1.playAudio(jcAudios[id])
            jcPlayerView1.createNotification(R.drawable.notimg)

//            jcPlayerView2.playAudio(jcAudios[id])
//            jcPlayerView2.createNotification(R.drawable.notimg)
        }

        val imageView2: ImageView = findViewById(R.id.imageView2)
        Picasso.get().load(thumbnail[id]).into(imageView2)
    }
}