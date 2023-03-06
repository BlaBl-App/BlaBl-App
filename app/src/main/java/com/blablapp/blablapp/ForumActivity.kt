package com.blablapp.blablapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private fun get_forums() {
        val apiThread = Thread {
            try {
                val forums = DAO.Companion.get_all_forums()
                for (forum in forums){
                    runOnUiThread{
                        forumList.add(Forum(forum.id, forum.name, forum.description))
                        forumAdapter.notifyDataSetChanged()
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