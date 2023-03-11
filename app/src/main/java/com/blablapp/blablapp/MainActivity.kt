package com.blablapp.blablapp

import android.app.Activity
import android.util.Base64
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var user : User = User("","","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_profil_activity)

        loadDataUser()
        pseudoEditable()

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
                val dateExpiration = sharedP.getString("date-expiration",null)

                //save the expiration time of the pseudo
                if(dateExpiration == null){
                    val dateExp = Calendar.getInstance()
                    dateExp.add(Calendar.MINUTE, 1)
                    sharedP.edit().putString("date-expiration", dateExp.timeInMillis.toString()).apply()
                }

                val intent: Intent = if (sharedP.getString("serverIp", "") == "") {
                    Intent(this, ServerConfig::class.java)
                } else {
                    DAO.setServIp(sharedP.getString("serverIp", "")!!, sharedP.getString("serverPort", "")!!, sharedP.getInt("serverProtocol", 0))
                    Intent(this, ForumActivity::class.java)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.messErrorPseudo), Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Load data-pseudo user from shared preferences
     * check if the current time is greater than the expiration time
     * if it is, the pseudo is editable
     * if not, the pseudo is not editable
     */
    private fun pseudoEditable() {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)

        val currentDate = Calendar.getInstance().timeInMillis
        val dateExpiration = sharedP.getString("date-expiration",null)

        if(dateExpiration != null) {
            val date = Date(dateExpiration.toLong()).time
            if (currentDate > date) {
                ProfilPseudo.isEnabled = true
                sharedP.edit().remove("date-expiration").apply()
            } else {
                ProfilPseudo.isEnabled = false
                ProfilPseudo.setText(this.user.pseudo)
            }
        }
    }


    /**
     * Listener for the result of the activity
     * if the result is ok, the image is set in the image view
     */
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            val tmpImage : Array<String> = uploadImageFromGallery(imageUri!!)
            this.user.linkImage = tmpImage[0]
            this.user.linkImageSmall = tmpImage[1]
            profilePic.setImageURI(imageUri)
        }
    }


    /**
     * This function open the gallery
     * and call the resultLauncher
     */
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        val file = getFile()
        val fileProvider = FileProvider.getUriForFile(this,"com.blablapp.blablapp", file)
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        galleryIntent.type = "image/*"
        resultLauncher.launch(galleryIntent)
    }


    /**
     * This override function is called when the user accept or refuse the permission
     */
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


    /**
     * This function upload the image from the gallery
     * @return the file created
     */
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

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        val compressedImageBytes: ByteArray = outputStream.toByteArray()
        val imageSmall: String = Base64.encodeToString(compressedImageBytes, Base64.NO_WRAP or Base64.URL_SAFE)



        return arrayOf( FileProvider.getUriForFile(this, "com.blablapp.blablapp", file).toString(),
            imageSmall
        )

    }


    /**
     * This function create a file in the picture directory
     * @return the file created
     */
    private fun createFileBM(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


    /**
     * This function is used by the OpenGallery function to get the temp file
     * @return the temp file created
     */
    private fun getFile(): File {
        val storageDirectory = getExternalFilesDir(DIRECTORY_PICTURES)
        return  File.createTempFile("image",".jpg",storageDirectory)
    }


    /**
     * This function save the data user in shared preferences
     */
    private fun saveDataUser() {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedP.edit()
        editor.putString("pseudo", this.user.pseudo)
        editor.putString("linkImage", this.user.linkImage)
        editor.putString("linkImageSmall", this.user.linkImageSmall)
        editor.apply()
    }


    /**
     * This function load the data user from shared preferences
     */
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


    /**
     * This function is called when the activity is stopped
     */
    override fun onStop() {
        super.onStop()
        saveDataUser()
    }


    /**
     * This function is called when the activity is resumed
     */
    override fun onResume() {
        super.onResume()
        pseudoEditable()
    }

}