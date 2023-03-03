package com.blablapp.blablapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.setup_profil_activity.*

@Suppress("DEPRECATION")
class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)

        bottomNavigationView.selectedItemId = R.id.about_us
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener)

    }

    private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.forum -> {
                val intent = Intent(this, ForumActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.about_us -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
