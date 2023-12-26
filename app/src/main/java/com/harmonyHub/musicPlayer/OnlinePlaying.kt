package com.harmonyHub.musicPlayer

import CommentAdapter
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harmonyHub.musicPlayer.databinding.ActivityOnlinePlayingBinding
import com.squareup.picasso.Picasso
import java.util.Timer
import java.util.TimerTask

class OnlinePlaying : AppCompatActivity() {

    private lateinit var thumbnail: MutableList<String>
    private lateinit var lyric: String
    private var duration: Int = -1
    private lateinit var jcPlayerView1: JcPlayerView
    private lateinit var jcAudios: List<JcAudio>
    private var currentSongId: Int = -1
    private var currentLyricIndex: Int = 0
    private var timer: Timer? = null

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var binding:ActivityOnlinePlayingBinding

    private lateinit var commentAdapter: CommentAdapter

    private lateinit var progressDialog: ProgressDialog

    private var songId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlinePlayingBinding.inflate(layoutInflater)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)

        supportActionBar?.hide()
        jcPlayerView1 = findViewById(R.id.jcplayer1)
        songId = intent.getIntExtra("songId", -1)
        thumbnail = intent.getStringArrayListExtra("thumbnail") as MutableList<String>
        jcAudios = intent.getParcelableArrayListExtra<JcAudio>("jcAudios") ?: emptyList()
        lyric = intent.getStringExtra("lyric") ?: ""
        duration = convertDurationToSeconds(intent.getStringExtra("duration"))

        commentAdapter = CommentAdapter()
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentsRecyclerView.adapter = commentAdapter

        if (songId != -1) {
            initializeJcPlayerViews(jcAudios, songId, thumbnail)
            startLyricScrolling()
        } else {
            Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
        }


        binding.addCommentBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                // user not logged in
                Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
            } else {
                showCommentsDialog()
                fetchComments()
            }
        }

        binding.backBtnOP.setOnClickListener { finish() }

//        commentAdapter.setOnItemClickListener { position ->
//            val clickedComment = commentAdapter.getItem(position)
//            // Handle clicked comment as needed
//        }


    }

    private fun showCommentsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.comments_dialog, null)

        val commentEditText: EditText = dialogView.findViewById(R.id.commentET)
        val submitButton: Button = dialogView.findViewById(R.id.submitBtn)

        // Set up the RecyclerView in the dialog
        val dialogRecyclerView: RecyclerView = dialogView.findViewById(R.id.dialogCommentsRecyclerView)
        dialogRecyclerView.layoutManager = LinearLayoutManager(this)
        dialogRecyclerView.adapter = commentAdapter

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Comments")

        val alertDialog = builder.create()

        // Set up the "Submit" button logic
        submitButton.setOnClickListener {
            val comment = commentEditText.text.toString()
            if (comment.isNotEmpty()) {
                // Call the function to add the comment
                addComment(comment)

                // If you want to dismiss the dialog after submitting the comment, uncomment the line below
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()
    }


    private fun addComment(comment: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val newComment = Comment(imageUrl = "", "Harmony Hub", comment, songId = "1")

            FirebaseFirestore.getInstance().collection("Comment")
                .add(newComment)
                .addOnSuccessListener {
                    Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show()
                    // Fetch and update comments after adding a new comment
                    fetchComments()
                }
                .addOnFailureListener { exception ->
                    Log.e("AddComment", "Error adding comment: ${exception.message}", exception)
                    Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLyricScrolling()
        progressDialog.dismiss()
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
            updateThumbnail(jcAudios[id])
            jcPlayerView1.playAudio(jcAudios[id])
            jcPlayerView1.createNotification(R.drawable.notimg)
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

    private fun fetchComments() {
        Log.d("FetchComments", "Fetching comments for songId: $currentSongId")
        FirebaseFirestore.getInstance().collection("Comment")
            .get()
            .addOnSuccessListener { result ->
                Log.d("FetchComments", "Success: ${result.size()} comments fetched")
                val comments = mutableListOf<Comment>()
                for (document in result) {
                    // Retrieve all fields from the document
                    val fields = document.data

                    // Print all fields
                    for ((key, value) in fields) {
                        Log.d("FetchComments", "$key: $value")
                    }

                    // Assuming you have fields like imageUrl, name, and comment
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val name = document.getString("name") ?: ""
                    val comment = document.getString("comment") ?: ""

                    comments.add(Comment(imageUrl, name, comment))
                }
                commentAdapter.setComments(comments)
            }
            .addOnFailureListener { exception ->
                Log.e("FetchComments", "Error fetching comments: ${exception.message}", exception)
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