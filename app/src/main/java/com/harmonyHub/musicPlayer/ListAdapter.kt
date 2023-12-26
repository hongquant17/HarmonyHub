package com.harmonyHub.musicPlayer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class ListAdapter(
    private val context: Context,
    private val songNames: MutableList<String?>,
    private val thumbnails: List<String?>?,
    private val songArtist: List<String?>?,
    private val songDuration: List<String?>?
) : BaseAdapter() {

    override fun getCount(): Int {
        return songNames?.size ?: 0
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.songs_list_layout, null)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val transformation: Transformation = RoundedTransformationBuilder()
            .cornerRadiusDp(15F)
            .build()

        Picasso.get().load(thumbnails?.get(i)).transform(transformation).into(viewHolder.thumbnail)
        viewHolder.songName.text = songNames?.get(i)
        viewHolder.artistName.text = songArtist?.get(i)
        viewHolder.songDuration.text = songDuration?.get(i)

        return convertView!!
    }

    private class ViewHolder(view: View) {
        val songName: TextView = view.findViewById(R.id.songName)
        val thumbnail: ImageView = view.findViewById(R.id.songThumbnail)
        val artistName: TextView = view.findViewById(R.id.artistName)
        val songDuration: TextView = view.findViewById(R.id.songDuration)
        val cardView: CardView = view.findViewById(R.id.cardView)
        val currentlyPlaying: ImageView = view.findViewById(R.id.currentlyPlaying)
    }
}