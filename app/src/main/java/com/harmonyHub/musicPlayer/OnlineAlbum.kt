package com.harmonyHub.musicPlayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class OnlineAlbum : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var songsNameList: MutableList<String?>
    private lateinit var songsUrlList: MutableList<String?>
    private lateinit var songsArtistList: MutableList<String?>
    private lateinit var songsDurationList: MutableList<String?>
    private lateinit var adapter: ListAdapter
    private lateinit var jcPlayerView: JcPlayerView
    private lateinit var jcAudios: MutableList<JcAudio>
    private lateinit var thumbnail: MutableList<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setContentView(R.layout.activity_online_album)
        val albumId = intent.getStringExtra("albumId")
        val albumTitle = intent.getStringExtra("albumTitle")
        val albumImg = intent.getStringExtra("albumImg")
        if (albumId != null) {
            val albumTitleTextView = findViewById<TextView>(R.id.albumTitleTextView)
            albumTitleTextView.text = albumTitle
            val albumImageView = findViewById<ImageView>(R.id.albumImg)
            Picasso.get().load(albumImg).into(albumImageView)
            initializeViews()
            retrieveSongs(albumId)
            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                jcPlayerView.visibility = View.VISIBLE
                jcPlayerView.createNotification()
                adapter.notifyDataSetChanged()
                val selectedSong = position
                val intent = Intent(this, OnlinePlaying::class.java)
                intent.putExtra("songId", selectedSong)
                intent.putExtra("thumbnail", ArrayList(thumbnail))
                intent.putParcelableArrayListExtra("jcAudios", ArrayList(jcAudios))
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "No album selected", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initializeViews() {
        listView = findViewById(R.id.songsList)
        songsNameList = mutableListOf()
        songsUrlList = mutableListOf()
        songsArtistList = mutableListOf()
        songsDurationList = mutableListOf()
        jcAudios = mutableListOf()
        thumbnail = mutableListOf()
        jcPlayerView = findViewById(R.id.jcplayer)

    }

    private fun retrieveSongs(albumId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Songs")
            .whereEqualTo("albumId", albumId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    for (document in querySnapshot) {
                        val songName = document.getString("songName")
                        val songUrl = document.getString("songUrl")
                        val songArtist = document.getString("songArtist")
                        val songDuration = document.getString("songDuration")
                        val imageUrl = document.getString("imageUrl")

                        songsNameList.add(songName)
                        songsUrlList.add(songUrl)
                        songsArtistList.add(songArtist)
                        songsDurationList.add(songDuration)
                        thumbnail.add(imageUrl)
                        jcAudios.add(JcAudio.createFromURL(songName.toString(), songUrl.toString()))
                    }

                    adapter = ListAdapter(
                        applicationContext,
                        songsNameList,
                        thumbnail,
                        songsArtistList,
                        songsDurationList
                    )

                    jcPlayerView.initPlaylist(jcAudios, null)
                    listView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@OnlineAlbum, "FAILED!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}