package com.blablapp.blablapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ForumAdapter(var context: Context, var listOfForum: ArrayList<Forum>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ForumViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.single_forum, parent, false)
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
        forumViewHolder.itemView.setOnLongClickListener{
            removeForum(position, forum.name, forum.id)
            true
        }
        forumViewHolder.itemView.setOnClickListener{
            selectForum(forum.id, forum.id)
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
            Log.d("DEBUG POSITION LIST", position.toString() )
            listOfForum.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
            apiThread.start()
        }
        alertDialog.setNegativeButton(R.string.no){dialog, which ->
            Toast.makeText(context, R.string.notDeletedForum, Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()
    }

    fun selectForum(position: Int, forumId: Int){
        var alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(R.string.forum)
        alertDialog.setMessage(R.string.selectForum)
        alertDialog.setPositiveButton(R.string.yes){dialog, which ->
            val sharedP = context.getSharedPreferences("user",
                AppCompatActivity.MODE_PRIVATE
            )
            val pseudo = sharedP.getString("pseudo", "")
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("idForum", forumId)
            intent.putExtra("user", pseudo)
            ContextCompat.startActivity(context, intent, null)
        }
        alertDialog.setNegativeButton(R.string.no){dialog, which ->
        }
        alertDialog.show()
    }

    class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val forumName = itemView.findViewById<TextView>(R.id.forumTitle)
        val forumDescription = itemView.findViewById<TextView>(R.id.forumDescription)
    }
}