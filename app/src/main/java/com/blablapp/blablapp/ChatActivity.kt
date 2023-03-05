package com.blablapp.blablapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.chat_activity.*

class ChatActivity : AppCompatActivity() {

    private var userName : String = ""
    private var linkImage : String = ""
    private var idForum : Int = -1
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var listOfMessage: ArrayList<UserMessage>
    private var lastMessageId: Int =-1
    private var liveUpdate = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        getMessage()

        listOfMessage = ArrayList()
        messageAdapter = MessageAdapter(this, listOfMessage)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        userName = intent?.extras?.getString("user").toString()
        linkImage = intent?.extras?.getString("linkImage").toString()
        idForum = intent?.extras?.getInt("idForum").toString().toInt()

        sendMsg.setOnClickListener{
            if (userTexForMsg.text?.isNotEmpty()!!) {
                sendMessage()
                val msg = userTexForMsg.text.toString()
                //getMessage()
                //listOfMessage.add(UserMessage(idForum, 1 , userName, linkImage, msg))

                // Scroll to the bottom of the list and show the new message
                messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                messageAdapter.notifyDataSetChanged()

                userTexForMsg.text!!.clear()
            }
        }
    }
    @Synchronized
    fun getLiveUpdate():Boolean{
        return liveUpdate
    }

    @Synchronized
    fun setLiveUpdate(newValue : Boolean){
        liveUpdate = newValue
    }
    @Synchronized
    fun getLastMessageId():Int{
        return lastMessageId
    }
    fun getMaxMessageId(messages : Array<Message>):Int{
        var maxId = Int.MIN_VALUE
        for (message in messages) {
            if (message.id > maxId) {
                maxId = message.id
            }
        }
        return maxId
    }

    @Synchronized
    fun setLastMessageId(newLastMessageId : Int){
        lastMessageId = newLastMessageId
    }

    fun getMessage(){

        val apiThread = Thread {
            try {
                while (getLiveUpdate()){
                    val servLastMessageId: Int = DAO.Companion.getLastMessageId()
                    if (getLastMessageId() != servLastMessageId ){
                        println("old messageid ${getLastMessageId()} new messageID $servLastMessageId")
                        val  messages : Array<Message> = DAO.Companion.getMessages()
                        messages.reverse()
                        setLastMessageId(getMaxMessageId(messages))
                        runOnUiThread {
                            listOfMessage.clear()
                        }
                        for (message in messages) {
                            runOnUiThread {
                                listOfMessage.add(UserMessage(idForum, message.postTime, message.nickname, linkImage, message.messageContent))
                                //MessageCustom(this, message.nickname, message.messageContent, layout)
                                messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                                messageAdapter.notifyDataSetChanged()
                            }

                        }
                        setLastMessageId(servLastMessageId)

                    }


                    Thread.sleep(300)

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