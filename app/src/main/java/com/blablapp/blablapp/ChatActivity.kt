package com.blablapp.blablapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.chat_activity.*

class ChatActivity : AppCompatActivity() {

    private var userName : String = ""
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var listOfMessage: ArrayList<UserMessage>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)


        getMessage()

        listOfMessage = ArrayList()
        messageAdapter = MessageAdapter(this, listOfMessage)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter



        sendMsg.setOnClickListener{
            if (userTexForMsg.text?.isNotEmpty()!!) {
                sendMessage()
                val msg = userTexForMsg.text.toString()
                userName = intent?.extras?.getString("user").toString()
                listOfMessage.add(UserMessage(null,null,userName,null, msg))

                // Scroll to the bottom of the list and show the new message
                messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                messageAdapter.notifyDataSetChanged()

                userTexForMsg.text!!.clear()
            }
        }
    }

    fun getMessage(){
        val apiThread = Thread {
            try {
                val  messages : Array<Message> = DAO.Companion.getMessages()

                for (message in messages) {
                    runOnUiThread {
                        MessageCustom(this, message.nickname, message.messageContent, layout)
                    }
                    
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // uncomment to activate api call test
        apiThread.start()

    }

    fun sendMessage(){
        val apiThread = Thread {
            try {
                //DAO.Companion.getMessages()
                val nickname =intent?.extras?.getString("user").toString()
                val message = userTexForMsg.text.toString()
                DAO.Companion.postMessages( nickname,"",message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // uncomment to activate api call test
        apiThread.start()
    }
}