package com.blablapp.blablapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.setup_profil_activity.*

@Suppress("DEPRECATION")
class ForumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forum_activity)

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


    /*private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
    }*/
}