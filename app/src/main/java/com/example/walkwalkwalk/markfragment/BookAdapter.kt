package com.example.walkwalkwalk.markfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.walkwalkwalk.databinding.BookmarkListItemBinding

class CustomViewHolder(val binding: BookmarkListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

class BookAdapter (val bookmarkList: ArrayList<BookMark>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return bookmarkList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder =
        CustomViewHolder(
            BookmarkListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CustomViewHolder).binding


    }
}