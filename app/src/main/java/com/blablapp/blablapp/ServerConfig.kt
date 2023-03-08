package com.blablapp.blablapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.content.SharedPreferences
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ip_address.*

class ServerConfig : AppCompatActivity() {

    private lateinit var sharedP: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ip_address)
        sharedP = applicationContext.getSharedPreferences("user", MODE_PRIVATE)
        editTextServer.setText(getServerIp())
        editTextPort.setText(getServerPort())
        spinnerProtolcol.setSelection(getServerProtocol())
        Log.d("DEBUG LAUNCH SERVER CONFIG", "HERE")
        buttonServer.setOnClickListener{
            if (editTextServer.text.toString() == ""){
                Toast.makeText(this, R.string.correctIp, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val serverIp = editTextServer.text.toString()
            val serverPort: String = if (editTextPort.text.toString() == ""){
                "5555"
            } else{
                editTextPort.text.toString()
            }

            DAO.setServIp(serverIp, serverPort, spinnerProtolcol.selectedItemPosition)
            saveDataUser(serverIp, serverPort, spinnerProtolcol.selectedItemPosition)
            val intent = Intent(this, ForumActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveDataUser(serverIp: String, serverPort: String = "5555", serverProtocol: Int) {
        val editor = sharedP.edit()
        editor.putString("serverIp", serverIp)
        editor.putString("serverPort", serverPort)
        editor.putInt("serverProtocol", serverProtocol)
        editor.apply()
    }

    private fun getServerIp(): String {
        return sharedP.getString("serverIp", "")!!
    }

    private fun getServerPort(): String {
        return sharedP.getString("serverPort", "")!!
    }

    private fun getServerProtocol(): Int {
        return sharedP.getInt("serverProtocol", 0)
    }
}