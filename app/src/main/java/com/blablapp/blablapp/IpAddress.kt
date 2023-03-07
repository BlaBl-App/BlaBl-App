package com.blablapp.blablapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ip_address.*

class IpAddress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ip_address)
        editTextServer.setText(getServerIp())
        buttonServer.setOnClickListener{
            if (editTextServer.text.toString() == "") {
                Toast.makeText(this, R.string.correctIp, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val serverIp = editTextServer.text.toString()
            DAO.Companion.setServIp(serverIp)
            saveDataUser(serverIp)
            val intent = Intent(this, ForumActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveDataUser(serverIp: String) {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedP.edit()
        editor.putString("serverIp", serverIp)
        editor.apply()
    }

    private fun getServerIp(): String {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        return sharedP.getString("serverIp", "")!!
    }
}