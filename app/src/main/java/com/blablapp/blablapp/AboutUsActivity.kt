package com.blablapp.blablapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.aboutus_activity.*
import kotlinx.android.synthetic.main.setup_profil_activity.*
import kotlinx.android.synthetic.main.setup_profil_activity.bottomNavigationView
import java.net.URL

@Suppress("DEPRECATION")
class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aboutus_activity)

        bottomNavigationView.selectedItemId = R.id.about_us
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener)

        imageViewIcePick4.setOnClickListener {
            navigationToGitHub("icepick4")
        }
        imageViewNabileSall.setOnClickListener {
            navigationToGitHub("nabilesall")
        }
        imageViewQypol342.setOnClickListener{
            navigationToGitHub("qypol342")
        }
    }

    private fun navigationToGitHub(id: String){
        val url = URL("https://github.com/".plus(id))
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
        startActivity(intent)
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
