package com.blablapp.blablapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.chat_activity.*
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class ChatActivity : AppCompatActivity() {

    private var userName : String = ""
    private var linkImage : String = ""
    private var idForum : Int = -1
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var listOfMessage: ArrayList<UserMessage>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        listOfMessage = ArrayList()
        messageAdapter = MessageAdapter(this, listOfMessage)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        userName = intent?.extras?.getString("user").toString()
        linkImage = intent?.extras?.getString("linkImage").toString()
        idForum = intent?.extras?.getInt("idForum").toString().toInt()


        sendMsg.setOnClickListener{
            if (userTexForMsg.text?.isNotEmpty()!!) {
                val msg = userTexForMsg.text.toString()

                listOfMessage.add(UserMessage(idForum, Date().time.milliseconds, userName, linkImage, msg))

                // Scroll to the bottom of the list and show the new message
                messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                messageAdapter.notifyDataSetChanged()

                userTexForMsg.text!!.clear()
            }
        }
    }
}