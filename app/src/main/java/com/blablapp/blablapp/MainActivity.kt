package com.blablapp.blablapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.setup_profil_activity.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var user : User = User("","", "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_profil_activity)

        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener OnNavigationItemSelectedListener@ { item ->
            when (item.itemId) {
                R.id.home -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.forum -> {
                    val intent = Intent(applicationContext, ForumActivity::class.java)
                    //intent.putExtra("user", this.user.pseudo)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.about_us -> {
                    val intent = Intent(applicationContext, AboutUsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        loadDataUser()

        profilePic.setOnClickListener{
            val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES else android.Manifest.permission.READ_EXTERNAL_STORAGE
            if(ContextCompat.checkSelfPermission(this, readImagePermission) == PackageManager.PERMISSION_GRANTED){
                //permission granted
                openGallery()
            }else{
                requestPermissions(arrayOf(readImagePermission), 1)
            }
        }

        buttonTalk.setOnClickListener {
            if (ProfilPseudo.text.toString() != "") {
                this.user.pseudo = ProfilPseudo.text.toString()
                val intent = Intent(this, IpAddress::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.messErrorPseudo), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.forum -> {
                val intent = Intent(this, ForumActivity::class.java)
                //intent.putExtra("user", this.user.pseudo)
                startActivity(intent)
                overridePendingTransition(0, 0)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageUri = data?.data
            this.user.linkImage = uploadImageFromGallery(imageUri!!)
            profilePic.setImageURI(imageUri)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        val file = getFile()
        val fileProvider = FileProvider.getUriForFile(this,"com.blablapp.blablapp", file)
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission non accordée", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageFromGallery(uri: Uri): String {
        val dataPath = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, dataPath, null, null, null)
        cursor!!.moveToFirst()
        val index = cursor.getColumnIndex(dataPath[0])
        val picturePath = cursor.getString(index)
        cursor.close()
        val bitmap = BitmapFactory.decodeFile(picturePath)
        val file = createFileBM()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
        return FileProvider.getUriForFile(this, "com.blablapp.blablapp", file).toString()

    }

    @SuppressLint("SimpleDateFormat")
    private fun createFileBM(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun getFile(): File {
        val storageDirectory = getExternalFilesDir(DIRECTORY_PICTURES)
        return  File.createTempFile("image",".jpg",storageDirectory)
    }

    private fun saveDataUser() {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedP.edit()
        editor.putString("pseudo", this.user.pseudo)
        editor.putString("linkImage", this.user.linkImage)
        editor.apply()
    }

    private fun loadDataUser() {
        this.user = User("","", "", "")
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val pseudo = sharedP.getString("pseudo", "")
        val linkImage = sharedP.getString("linkImage", "")
        val serverIp = sharedP.getString("serverIp", "")
        val serverPort = sharedP.getString("serverPort", "")
        if (pseudo != null && linkImage != null && serverIp != null && serverPort != null) {
            this.user = User(pseudo, linkImage, serverIp, serverPort)
            if (this.user.linkImage.isNotEmpty()){
                profilePic.setImageURI(this.user.linkImage.toUri())
            }else{
                profilePic.setImageResource(R.drawable.defaultpp)
            }
            ProfilPseudo.setText(this.user.pseudo)
        }
    }

    override fun onStop() {
        super.onStop()
        saveDataUser()
    }

}