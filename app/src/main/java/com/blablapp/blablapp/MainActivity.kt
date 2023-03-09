package com.blablapp.blablapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.setup_profil_activity.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var user : User = User("","","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_profil_activity)

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
                val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
                val intent: Intent
                if (sharedP.getString("serverIp", "") == "") {
                    intent = Intent(this, ServerConfig::class.java)
                } else {
                    DAO.setServIp(sharedP.getString("serverIp", "")!!, sharedP.getString("serverPort", "")!!, sharedP.getInt("serverProtocol", 0))
                    intent = Intent(this, ForumActivity::class.java)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.messErrorPseudo), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            val tmpImage : Array<String> = uploadImageFromGallery(imageUri!!)
            this.user.linkImage = tmpImage.get(0)
            this.user.linkImageSmall = tmpImage.get(1)
            profilePic.setImageURI(imageUri)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        val file = getFile()
        val fileProvider = FileProvider.getUriForFile(this,"com.blablapp.blablapp", file)
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        galleryIntent.type = "image/*"
        resultLauncher.launch(galleryIntent)
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

    private fun uploadImageFromGallery(uri: Uri): Array<String> {
        val dataPath = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, dataPath, null, null, null)
        cursor!!.moveToFirst()
        val index = cursor.getColumnIndex(dataPath[0])
        val picturePath = cursor.getString(index)
        cursor.close()
        val bitmap = BitmapFactory.decodeFile(picturePath)
        val file = createFileBM()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
        val bitmapSmall = BitmapFactory.decodeFile(picturePath)
        val fileSmall = createFileBM()
        bitmapSmall.compress(Bitmap.CompressFormat.JPEG, 10, fileSmall.outputStream())
        return arrayOf( FileProvider.getUriForFile(this, "com.blablapp.blablapp", file).toString(),
            FileProvider.getUriForFile(this, "com.blablapp.blablapp", fileSmall).toString()
        )

    }

    private fun createFileBM(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
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
        editor.putString("linkImageSmall", this.user.linkImageSmall)
        editor.apply()
    }

    private fun loadDataUser() {
        this.user = User("","","")
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val pseudo = sharedP.getString("pseudo", "")
        val linkImage = sharedP.getString("linkImage", "")
        val linkImageSmall: String = sharedP.getString("linkImageSmall", "").toString()
        if (pseudo != null && linkImage != null) {
            this.user = User(pseudo, linkImage, linkImageSmall)
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