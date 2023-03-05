package com.blablapp.blablapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.single_forum.view.*

class ForumAdapter(var context: Context, var listOfForum: ArrayList<Forum>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ForumViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_forum, null)
        )
    }

    override fun getItemCount(): Int {
        return listOfForum.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val forum = listOfForum[position]
        val forumViewHolder = holder as ForumViewHolder
        forumViewHolder.forumName.text = forum.name
        forumViewHolder.forumDescription.text = forum.description
    }

    class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val forumName = itemView.findViewById<TextView>(R.id.forumTitle)
        val forumDescription = itemView.findViewById<TextView>(R.id.forumDescription)
    }
}