package com.blablapp.blablapp

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

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
        Log.d("DEBUG FORUM", forum.toString())
        val forumViewHolder = holder as ForumViewHolder
        forumViewHolder.forumName.text = forum.name
        forumViewHolder.forumDescription.text = forum.description
        forumViewHolder.itemView.setOnLongClickListener{
            removeForum(position, forum.name, forum.id)
            true
        }
    }

    fun removeForum(position: Int, forumName: String, forumId: Int){
        var apiThread = Thread{
            try{
                DAO.Companion.removeForum(forumId)
            }
            catch (e: Exception){
                Log.d("DEBUG", e.toString())
            }
        }
        var alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(R.string.removeForum)
        alertDialog.setMessage(R.string.askRemoveForum)
        alertDialog.setPositiveButton(R.string.yes){dialog, which ->
            Toast.makeText(context, R.string.deletedForum, Toast.LENGTH_SHORT).show()
            listOfForum.removeAt(position)
            notifyItemRemoved(position)
            apiThread.start()
        }
        alertDialog.setNegativeButton(R.string.no){dialog, which ->
            Toast.makeText(context, R.string.notDeletedForum, Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()
    }

    class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val forumName = itemView.findViewById<TextView>(R.id.forumTitle)
        val forumDescription = itemView.findViewById<TextView>(R.id.forumDescription)
    }
}