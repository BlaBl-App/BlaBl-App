package com.blablapp.blablapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.chat_activity.*

class ChatActivity : AppCompatActivity() {

    private var userName : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        sendMsg.setOnClickListener{
            if (userTexForMsg.text?.isNotEmpty()!!) {
                val msg = userTexForMsg.text.toString()

                userName = intent?.extras?.getString("user").toString()
                MessageCustom(this, userName, msg, layout)

                userTexForMsg.text!!.clear()
            }
        }
    }
}