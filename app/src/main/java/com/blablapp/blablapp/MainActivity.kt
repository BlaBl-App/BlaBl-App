package com.blablapp.blablapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.setup_profil_activity.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var user : User = User("","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_profil_activity)

        loadDataUser()

        if (this.user.linkImage.length != 0){
            profilePic.setImageURI(this.user.linkImage.toUri())
        }

        pseudo.setText(this.user.pseudo)

        profilePic.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK)
            val file = getFile("image")
            val fileProvider = FileProvider.getUriForFile(this,"com.blablapp.blablapp", file)
            galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)

            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 1)
        }

        buttonTalk.setOnClickListener {
            if (pseudo.text.toString() != "") {
                this.user.pseudo = pseudo.text.toString()
                val intent = Intent(this, ForumActivity::class.java)
                intent.putExtra("user", this.user)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.messErrorPseudo), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageUri = data?.data
            this.user.linkImage = uploadImageFromGallery(imageUri!!)

            profilePic.setImageURI(imageUri)

            //Toast.makeText(this, "Image enregistrée", Toast.LENGTH_SHORT).show()
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
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

    private fun getFile(nomFichier: String): File {
        val strgDirec = getExternalFilesDir(DIRECTORY_PICTURES)
        return  File.createTempFile(nomFichier,".jpg",strgDirec)
    }

    private fun saveDataUser() {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedP.edit()
        editor.putString("pseudo", this.user.pseudo)
        editor.putString("linkImage", this.user.linkImage)
        editor.apply()
        loadDataUser()
    }

    private fun loadDataUser() {
        this.user = User("","")
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val pseudo = sharedP.getString("pseudo", "")
        val linkImage = sharedP.getString("linkImage", "")
        if (pseudo != null && linkImage != null) {
            this.user = User(pseudo, linkImage)
        }
    }

    override fun onStop() {
        super.onStop()
        saveDataUser()
    }
}