package com.harmonyHub.musicPlayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class AlbumAdapter(
    private val context: Context,
    private val albums: List<Album>
) : BaseAdapter() {

    override fun getCount(): Int {
        return albums.size
    }

    override fun getItem(position: Int): Any {
        return albums[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val viewHolder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.album_list_item, parent, false)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
        } else {
            viewHolder = itemView.tag as ViewHolder
        }

        val album = albums[position]

        viewHolder.albumTitle.text = album.title
        Picasso.get().load(album.coverImageUrl).into(viewHolder.albumCover)

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val albumCover: ImageView = view.findViewById(R.id.albumCover)
        val albumTitle: TextView = view.findViewById(R.id.albumTitle)
    }
}