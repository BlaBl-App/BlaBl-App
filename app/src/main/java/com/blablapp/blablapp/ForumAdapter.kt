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

class ForumAdapter(private var context: Context, private var listOfForum: ArrayList<Forum>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            removeForum(position, forum.id)
            true
        }
        forumViewHolder.itemView.setOnClickListener{
            selectForum(forum.id)
        }
    }

    private fun removeForum(position: Int, forumId: Int){
        val apiThread = Thread{
            try{
                DAO.removeForum(forumId)
            }
            catch (e: Exception){
                Log.d("DEBUG", e.toString())
            }
        }
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(R.string.removeForum)
        alertDialog.setMessage(R.string.askRemoveForum)
        alertDialog.setPositiveButton(R.string.yes){_, _ ->
            Toast.makeText(context, R.string.deletedForum, Toast.LENGTH_SHORT).show()
            listOfForum.removeAt(position)
            notifyItemRemoved(position)
            apiThread.start()
        }
        alertDialog.setNegativeButton(R.string.no){_, _ ->
            Toast.makeText(context, R.string.notDeletedForum, Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()
    }

    private fun selectForum(forumId: Int){
        val sharedP = context.getSharedPreferences("user",
            AppCompatActivity.MODE_PRIVATE
        )
        val pseudo = sharedP.getString("pseudo", "")
        val linkImage = sharedP.getString("linkImage", "")
        val linkImageSmall = sharedP.getString("linkImageSmall", "")
        val intent = Intent(context, ChatActivity::class.java)

        intent.putExtra("idForum", forumId)
        intent.putExtra("user", pseudo)
        intent.putExtra("linkImage", linkImage)
        intent.putExtra("linkImageSmall", linkImageSmall)
        ContextCompat.startActivity(context, intent, null)
    }

    class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val forumName: TextView = itemView.findViewById(R.id.forumTitle)
        val forumDescription: TextView = itemView.findViewById(R.id.forumDescription)
    }
}