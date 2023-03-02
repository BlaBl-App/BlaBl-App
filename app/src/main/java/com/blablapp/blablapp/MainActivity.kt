package com.blablapp.blablapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.menu_activity.*
import kotlinx.android.synthetic.main.setup_profil_activity.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var user : User = User("","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        val intent = Intent(this, SetupProfileActivity::class.java)
        startActivity(intent)

        navigationView.setOnNavigationItemSelectedListener(navigationListener)

        //loadDataUser()

        /**/
    }

    private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                val intent = Intent(this, SetupProfileActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.forum -> {
                val intent = Intent(this, ForumActivity::class.java)
                intent.putExtra("user", this.user.pseudo)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.about_us -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AboutFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


}