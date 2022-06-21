package com.bangkit.storyapp.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.ListStoryItem
import com.bangkit.storyapp.databinding.ItemStoryListBinding
import com.bumptech.glide.Glide

class StoryListAdapter: PagingDataAdapter<ListStoryItem, StoryListAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(private var binding: ItemStoryListBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(data: ListStoryItem) {

            with(binding) {
                Glide.with(imagePhoto.context)
                    .load(data.photoUrl)
                    .into(imagePhoto)
                textName.text = data.name
                imagePhoto.setOnClickListener {

                    val moveIntent = Intent(itemView.context, DetailActivity::class.java)
                    moveIntent.putExtra(DetailActivity.PHOTO_URL, data.photoUrl)
                    moveIntent.putExtra(DetailActivity.NAME, data.name)
                    moveIntent.putExtra(DetailActivity.DESCRIPTION, data.description)

                    val optionCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imagePhoto, "image"),
                            Pair(textName, "name")
                        )

                    itemView.context.startActivity(moveIntent, optionCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ListStoryItem>() {

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}