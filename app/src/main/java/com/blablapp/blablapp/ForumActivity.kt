package com.blablapp.blablapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.forum_activity.*

class ForumActivity : AppCompatActivity() {

    private lateinit var forumAdapter : ForumAdapter
    private lateinit var forumList : ArrayList<Forum>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forum_activity)

        getForums()
        forumList = ArrayList()
        forumAdapter = ForumAdapter(this, forumList)
        forumRecyclerView.layoutManager = LinearLayoutManager(this)
        forumRecyclerView.adapter = forumAdapter

        addForumButton.setOnClickListener{
            addForumDialog()
        }

        changeServerButton.setOnClickListener{
            val intent = Intent(this, ServerConfig::class.java)
            startActivity(intent)
        }

    }

    private fun addForumDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_forum, null)
        val inputName = dialogView.findViewById<EditText>(R.id.inputName)
        val inputDescription = dialogView.findViewById<EditText>(R.id.inputDescription)

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.addForum)
            .setMessage(R.string.addForumQuestion)
            .setView(dialogView)
            .setPositiveButton(R.string.add) { _, _ ->
                val forumName = inputName.text.toString()
                val forumDescription = inputDescription.text.toString()
                val apiThread = Thread {
                    try {
                        DAO.addForum(forumName, forumDescription)
                        runOnUiThread {
                            getForums()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                apiThread.start()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
        dialog.show()
    }

    private fun getForums() {
        val apiThread = Thread {
            try {
                val forums = DAO.getAllForums()
                for (forum in forums){
                    runOnUiThread{
                        //no connection
                        if (forum.id == -1){
                            val alert = AlertDialog.Builder(this)
                            alert.setTitle(R.string.noConnectionTitle)
                            alert.setMessage(R.string.noConnection)
                            alert.setPositiveButton("OK") { _, _ ->
                                val intent = Intent(this, ServerConfig::class.java)
                                startActivity(intent)
                            }
                            alert.show()
                            return@runOnUiThread
                        }
                        //check if the server is already in the list
                        var alreadyInList = false
                        for (forumInList in forumList){
                            if (forumInList.id == forum.id){
                                alreadyInList = true
                            }
                        }
                        if (!alreadyInList){
                            forumList.add(forum)
                            forumAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            catch(e: Exception) {
                e.printStackTrace()
            }
        }
        apiThread.start()
    }
}