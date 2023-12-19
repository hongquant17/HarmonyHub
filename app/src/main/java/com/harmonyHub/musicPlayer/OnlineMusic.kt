    package com.harmonyHub.musicPlayer
    import android.content.Intent
    import android.os.Bundle
    import android.widget.AdapterView
    import android.widget.AdapterView.OnItemClickListener
    import android.widget.ListView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.example.jean.jcplayer.model.JcAudio
    import com.example.jean.jcplayer.view.JcPlayerView
    import com.google.firebase.firestore.FirebaseFirestore

    class OnlineMusic : AppCompatActivity() {

        private lateinit var topListView: ListView
        private lateinit var bottomListView: ListView
        private lateinit var songsNameList: MutableList<String?>
        private lateinit var songsUrlList: MutableList<String?>
        private lateinit var songsArtistList: MutableList<String?>
        private lateinit var songsDurationList: MutableList<String?>
        private lateinit var jcPlayerView: JcPlayerView
        private lateinit var jcAudios: MutableList<JcAudio>
        private lateinit var thumbnail: MutableList<String?>
        private lateinit var albumList: MutableList<Album>
        private lateinit var albumAdapter: AlbumAdapter
        private lateinit var albumAdapterTop: AlbumAdapter
        private lateinit var albumAdapterBottom: AlbumAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setContentView(R.layout.activity_online_music)

            initializeViews()
            retrieveAlbums()

            topListView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                albumAdapter.notifyDataSetChanged()
                val selectedAlbum = (position + 1).toString()
                val intent = Intent(this, OnlineAlbum::class.java)
                intent.putExtra("albumTitle", albumList[position].title)
                intent.putExtra("albumImg", albumList[position].coverImageUrl)
                intent.putExtra("albumId", selectedAlbum)
                startActivity(intent)
            }
            bottomListView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                albumAdapter.notifyDataSetChanged()
                val selectedAlbum = (position + 4).toString()
                val intent = Intent(this, OnlineAlbum::class.java)
                intent.putExtra("albumTitle", albumList[position+3].title)
                intent.putExtra("albumImg", albumList[position+3].coverImageUrl)
                intent.putExtra("albumId", selectedAlbum)
                startActivity(intent)
            }
        }

        private fun initializeViews() {
            topListView = findViewById(R.id.topListView)
            bottomListView = findViewById(R.id.bottomListView)
            songsNameList = mutableListOf()
            songsUrlList = mutableListOf()
            songsArtistList = mutableListOf()
            songsDurationList = mutableListOf()
            jcAudios = mutableListOf()
            thumbnail = mutableListOf()
            jcPlayerView = findViewById(R.id.jcplayer)
            albumList = mutableListOf()
            albumAdapter = AlbumAdapter(this, albumList)

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

                        // Hiển thị danh sách đầu gồm 3 album
                        val firstThreeAlbums = albumList.take(3)
                        albumAdapterTop = AlbumAdapter(this, firstThreeAlbums.toMutableList())
                        topListView.adapter = albumAdapterTop

                        // Hiển thị danh sách cuối cùng gồm 3 album
                        val lastThreeAlbums = albumList.takeLast(3)
                        albumAdapterBottom = AlbumAdapter(this, lastThreeAlbums.toMutableList())
                        bottomListView.adapter = albumAdapterBottom
                    } else {
                        Toast.makeText(this, "Failed to retrieve albums", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
