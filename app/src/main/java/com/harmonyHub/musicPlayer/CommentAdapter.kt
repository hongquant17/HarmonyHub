import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harmonyHub.musicPlayer.Comment
import com.harmonyHub.musicPlayer.R

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val comments = mutableListOf<Comment>()
    private var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        // Bind data to views
        holder.commentNameTextView.text = comment.name
        holder.commentTextView.text = comment.comment
        // Load image using your preferred image loading library (Glide, Picasso, etc.)
        // Example using Glide:
        Glide.with(holder.itemView)
            .load(comment.imageUrl)
            .placeholder(R.drawable.notimg)
            .into(holder.commentImageView)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = comments.size

    fun setComments(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentImageView: ImageView = itemView.findViewById(R.id.commentImageView)
        val commentNameTextView: TextView = itemView.findViewById(R.id.commentNameTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
    }
}
