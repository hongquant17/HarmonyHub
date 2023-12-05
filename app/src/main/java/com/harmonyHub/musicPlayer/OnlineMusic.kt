package com.harmonyHub.musicPlayer

import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.firestore.FirebaseFirestore

class OnlineMusic : AppCompatActivity() {

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
        setContentView(R.layout.activity_online_music)

        listView = findViewById(R.id.songsList)
        songsNameList = mutableListOf()
        songsUrlList = mutableListOf()
        songsArtistList = mutableListOf()
        songsDurationList = mutableListOf()
        jcAudios = mutableListOf()
        thumbnail = mutableListOf()
        jcPlayerView = findViewById(R.id.jcplayer)

        retrieveSongs()

        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            jcPlayerView.playAudio(jcAudios[i])
            jcPlayerView.visibility = View.VISIBLE
            jcPlayerView.createNotification()
            adapter.notifyDataSetChanged()
        }
    }

    // RETRIEVING THE SONGS FROM THE SERVER
    private fun retrieveSongs() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Songs")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val song = document.toObject(Song::class.java)
                    songsNameList.add(song.songName)
                    songsUrlList.add(song.songUrl)
                    songsArtistList.add(song.songArtist)
                    songsDurationList.add(song.songDuration)
                    thumbnail.add(song.imageUrl)
                    jcAudios.add(JcAudio.createFromURL(song.songName.toString(),
                        song.songUrl.toString()
                    ))
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
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@OnlineMusic, "FAILED!", Toast.LENGTH_SHORT).show()
            }
    }

}