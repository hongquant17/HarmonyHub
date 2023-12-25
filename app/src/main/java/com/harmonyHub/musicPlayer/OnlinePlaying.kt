package com.harmonyHub.musicPlayer

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView
import com.google.firebase.auth.FirebaseAuth
import com.harmonyHub.musicPlayer.databinding.ActivityOnlinePlayingBinding
import com.harmonyHub.musicPlayer.databinding.DialogCommentAddBinding
import com.squareup.picasso.Picasso

class OnlinePlaying : AppCompatActivity() {

    private lateinit var thumbnail: MutableList<String>
    private lateinit var jcPlayerView1: JcPlayerView
    private lateinit var jcAudios: List<JcAudio>
    private var currentSongId: Int = -1

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var binding:ActivityOnlinePlayingBinding

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

        if (songId != -1) {
            initializeJcPlayerViews(jcAudios, songId, thumbnail)
        } else {
            Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
        }

        // handle click, show add comment dialog
        binding.addCommentBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                // user not logged in
                Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
            } else {
                addCommentDialog()
            }
        }
    }

    private var comment = ""

    private fun addCommentDialog() {
        //inflate/bind view for dialog_comment_add.xml
        val commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this))

        //set up alert dialog
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(R.layout.dialog_comment_add)

        // create and show alert dialog
        var alertDialog = builder.create()
        alertDialog.show()

        //handle click, dismiss dialog
        commentAddBinding.backBtn.setOnClickListener{ alertDialog.dismiss() }

        // handle click, add comment
//        commentAddBinding.submitBtn.setOnClickListener {
//            comment = commentAddBinding.commentET.text.toString().trim()
//            //validate data
//            if (comment.isEmpty()) {
//                Toast.makeText(this, "Enter comment...", Toast.LENGTH_SHORT).show()
//            } else {
//                alertDialog.dismiss()
////                addComment()
//            }
//        }
    }

//    private fun addComment() {
//        //show progress
//        progressDialog.apply {
//            setMessage("Adding Comment")
//            show()
//        }
//
//        //timestamp
//        val timestamp = "${System.currentTimeMillis()}"
//
//        //set up data to add in db for comment
//        val hashMap = HashMap<String, Any>()
//        hashMap["id"] = "$timestamp"
//        hashMap["songId"] = "$songId"
//        hashMap["timestamp"] = "$timestamp"
//        hashMap["comment"] = "$comment"
//        hashMap["uid"] = "${firebaseAuth.uid}"
//
//        // db path to add data
////        val ref = FirebaseFirestore.getInstance().collection("Songs").document(songId.toString())
////        ref.collection("Comments").document(timestamp)
////            .set(hashMap)
////            .addOnSuccessListener {
////                progressDialog.dismiss()
////                Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()
////            }
////            .addOnFailureListener { e ->
////                progressDialog.dismiss()
////                Toast.makeText(this, "Comment not added  ${e.message}", Toast.LENGTH_SHORT).show()
////                Log.e("AddComment", "Failed to add comment: ${e.message}", e)
////            }
//    }



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

    private fun updateThumbnail(jcAudio: JcAudio?) {
        jcAudio?.let {
            val position = jcAudios.indexOf(it)
            if (position != -1 && position < thumbnail.size) {
                val imageView2: ImageView = findViewById(R.id.imageView2)
                Picasso.get().load(thumbnail[position]).into(imageView2)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Dismiss ProgressDialog if it's showing
        progressDialog.dismiss()
    }
}