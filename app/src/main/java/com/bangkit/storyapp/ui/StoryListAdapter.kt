package com.bangkit.storyapp.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.R
import com.bumptech.glide.Glide

class StoryListAdapter (private val listStory: ArrayList<StoryList>) : RecyclerView.Adapter<StoryListAdapter.ListViewHolder>() {
    class ListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagePhoto: ImageView = itemView.findViewById(R.id.image_photo)
        var textName: TextView = itemView.findViewById(R.id.text_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_story_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photoUrl) = listStory[position]
        Glide.with(holder.imagePhoto.context)
            .load(photoUrl)
            .into(holder.imagePhoto)
        holder.textName.text = name
        holder.imagePhoto.setOnClickListener {
            val moveIntent = Intent(holder.itemView.context, DetailActivity::class.java)
            moveIntent.putExtra(DetailActivity.PHOTO_URL, photoUrl)
            moveIntent.putExtra(DetailActivity.NAME, name)
            moveIntent.putExtra(DetailActivity.DESCRIPTION, description)

            val optionCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.imagePhoto, "image"),
                    Pair(holder.textName, "name")
                )
            holder.itemView.context.startActivity(moveIntent, optionCompat.toBundle())
        }
    }

    override fun getItemCount(): Int = listStory.size

}