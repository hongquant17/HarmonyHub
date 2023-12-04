package com.harmonyHub.musicPlayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OnlineMusic : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_music)
        supportActionBar?.hide() // For Hiding The Action bar

        fetchSongsFromFirestore()
    }

    private fun fetchSongsFromFirestore() {
        GlobalScope.launch(Dispatchers.IO) {
            val collection = firestore.collection("songs")
            val querySnapshot = collection.get().await()

            val jcAudios = ArrayList<JcAudio>()

            for (document in querySnapshot.documents) {
                val title = document.getString("title") ?: "Unknown"
                val audioUrl = document.getString("audioUrl") ?: ""

                val jcAudio = JcAudio.createFromURL(title, audioUrl)
                jcAudios.add(jcAudio)
            }

            runOnUiThread {
                initializeJcPlayerViews(jcAudios)
            }
        }
    }

    private fun initializeJcPlayerViews(jcAudios: List<JcAudio>) {
        val jcPlayerView1 = findViewById<JcPlayerView>(R.id.jcplayer1)
        jcPlayerView1.initPlaylist(jcAudios, null)
        jcPlayerView1.createNotification(R.drawable.notimg)

        val jcPlayerView2 = findViewById<JcPlayerView>(R.id.jcplayer2)
        jcPlayerView2.initPlaylist(jcAudios, null)
        jcPlayerView2.createNotification(R.drawable.notimg)
    }
}
