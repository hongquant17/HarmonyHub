package com.harmonyHub.musicPlayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.firestore.FirebaseFirestore

class OnlineMusic : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var albumView: ListView
    private lateinit var songsNameList: MutableList<String?>
    private lateinit var songsUrlList: MutableList<String?>
    private lateinit var songsArtistList: MutableList<String?>
    private lateinit var songsDurationList: MutableList<String?>
    private lateinit var adapter: ListAdapter
    private lateinit var jcPlayerView: JcPlayerView
    private lateinit var jcAudios: MutableList<JcAudio>
    private lateinit var thumbnail: MutableList<String?>
    private lateinit var albumList: MutableList<Album>
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_music)

        initializeViews()
        retrieveSongs()
        retrieveAlbums()

        listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
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

        albumView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            albumAdapter.notifyDataSetChanged()
            val selectedAlbum = (position +1).toString()
            val intent = Intent(this, OnlineAlbum::class.java)
            intent.putExtra("albumId", selectedAlbum)
            startActivity(intent)
        }
    }

    private fun initializeViews() {
        listView = findViewById(R.id.songsList)
        albumView = findViewById(R.id.albumList)
        songsNameList = mutableListOf()
        songsUrlList = mutableListOf()
        songsArtistList = mutableListOf()
        songsDurationList = mutableListOf()
        jcAudios = mutableListOf()
        thumbnail = mutableListOf()
        jcPlayerView = findViewById(R.id.jcplayer)
        albumList = mutableListOf()
        albumAdapter = AlbumAdapter(this, albumList)
        val albumListView = findViewById<ListView>(R.id.albumList)
        albumListView.adapter = albumAdapter
    }

    private fun retrieveSongs() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Songs")
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
                    Toast.makeText(this@OnlineMusic, "FAILED!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun retrieveAlbums() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("albums")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    for (document in querySnapshot) {
                        val albumTitle = document.getString("title")
                        val albumCoverImageUrl = document.getString("CoverImageUrl")
                        val album = Album(albumTitle, albumCoverImageUrl)
                        albumList.add(album)
                    }
                    albumAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Failed to retrieve albums", Toast.LENGTH_SHORT).show()
                }
            }
    }
}