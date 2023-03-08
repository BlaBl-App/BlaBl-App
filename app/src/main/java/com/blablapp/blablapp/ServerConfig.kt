package com.blablapp.blablapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ip_address.*

class ServerConfig : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ip_address)
        editTextServer.setText(getServerIp())
        editTextPort.setText(getServerPort())
        spinnerProtolcol.setSelection(getServerProtocol())
        buttonServer.setOnClickListener{
            if (editTextServer.text.toString() == "") {
                Toast.makeText(this, R.string.correctIp, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val serverIp = editTextServer.text.toString()
            val serverPort = editTextPort.text.toString()
            // 0 = http, 1 = https
            val serverProtocol: String = if (spinnerProtolcol.selectedItemPosition == 0){
                "http://"
            } else{
                "https://"
            }
            DAO.setServIp(serverIp, serverPort, serverProtocol)
            saveDataUser(serverIp, serverPort, spinnerProtolcol.selectedItemPosition)
            val intent = Intent(this, ForumActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveDataUser(serverIp: String, serverPort: String = "8080", serverProtocol: Int) {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedP.edit()
        editor.putString("serverIp", serverIp)
        editor.putString("serverPort", serverPort)
        editor.putInt("serverProtocol", serverProtocol)
        editor.apply()
    }

    private fun getServerIp(): String {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        return sharedP.getString("serverIp", "")!!
    }

    private fun getServerPort(): String {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        return sharedP.getString("serverPort", "")!!
    }

    private fun getServerProtocol(): Int {
        val sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        return sharedP.getInt("serverProtocol", 0)
    }
}