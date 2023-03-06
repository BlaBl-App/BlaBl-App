package com.blablapp.blablapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.forum_activity.*
import kotlinx.android.synthetic.main.setup_profil_activity.*
import kotlinx.android.synthetic.main.setup_profil_activity.bottomNavigationView

@Suppress("DEPRECATION")
class ForumActivity : AppCompatActivity() {

    private lateinit var forumAdapter : ForumAdapter
    private lateinit var forumList : ArrayList<Forum>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forum_activity)

        get_forums()
        forumList = ArrayList()
        forumAdapter = ForumAdapter(this, forumList)
        forumRecyclerView.layoutManager = LinearLayoutManager(this)
        forumRecyclerView.adapter = forumAdapter

        addForumButton.setOnClickListener{
            addForumDialog()
        }

        bottomNavigationView.selectedItemId = R.id.forum
        bottomNavigationView.setOnNavigationItemSelectedListener OnNavigationItemSelectedListener@{ item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    //intent.putExtra("user", this.user.pseudo)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.forum -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.about_us -> {
                    val intent = Intent(this, AboutUsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
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
            .setPositiveButton(R.string.yes) { _, _ ->
                val forumName = inputName.text.toString()
                val forumDescription = inputDescription.text.toString()
                val apiThread = Thread {
                    try {
                        val forum = DAO.Companion.addForum(forumName, forumDescription)
                        runOnUiThread {
                            get_forums()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                apiThread.start()
            }
            .setNegativeButton(R.string.no) { _, _ -> }
            .create()

        dialog.show()
    }

    private fun get_forums() {
        val apiThread = Thread {
            try {
                val forums = DAO.Companion.get_all_forums()
                for (forum in forums){
                    runOnUiThread{
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