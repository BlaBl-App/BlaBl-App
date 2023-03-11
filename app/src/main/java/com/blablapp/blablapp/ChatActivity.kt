package com.blablapp.blablapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.chat_activity.*

class ChatActivity : AppCompatActivity() {

    private var userName : String = ""
    private var linkImage : String = ""
    private var idForum : Int = -1
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var listOfMessage: ArrayList<Message>
    private var lastMessageId: Int =-1
    private var liveUpdate = true
    private var nbMessageToShow = 10
    private var noPullDown :Boolean = false


    override fun onStop() {
        super.onStop()
        setLiveUpdate(false)
    }

    override fun onResume() {
        super.onResume()
        if (!getLiveUpdate()){
            setLiveUpdate(true)
            launchMessageProcess()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        //if swiping down, refresh the list of messages (+5)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            nbMessageToShow += 5
            noPullDown = true
            setLastMessageId(-1)
            swipeRefreshLayout.isRefreshing = false
        }

        launchMessageProcess()

        listOfMessage = ArrayList()
        messageAdapter = MessageAdapter(this,listOfMessage)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        userName = intent?.extras?.getString("user").toString()
        linkImage = intent?.extras?.getString("linkImageSmall").toString()
        idForum = intent?.extras?.getInt("idForum").toString().toInt()


        sendMsg.setOnClickListener{
            if (userTexForMsg.text?.isNotEmpty()!!){
                val message = userTexForMsg.text.toString()
                sendMessage(message,linkImage, idForum)
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
    private fun getMaxMessageId(messages: Array<Message>): Int {
        return messages.maxByOrNull { it.id }?.id ?: Int.MIN_VALUE
    }

    @Synchronized
    fun setLastMessageId(newLastMessageId : Int){
        lastMessageId = newLastMessageId
    }


    /**
     * Get the messages from the server
     */
    private fun launchMessageProcess(){

        val apiThread = Thread {
            try {
                while (getLiveUpdate()){
                    val servLastMessageId: Int = DAO.getLastMessageId(idForum)
                    if (getLastMessageId() != servLastMessageId ){
                        Log.d("DEBUG GET MESSAGES","old message id ${getLastMessageId()} new messageID $servLastMessageId pull status $noPullDown")
                        val  messages : Array<Message> = DAO.getMessages(nb=nbMessageToShow, forum=idForum)
                        messages.reverse()

                        (getMaxMessageId(messages))
                        runOnUiThread {
                            listOfMessage.clear()
                        }
                        for (message in messages) {
                            runOnUiThread {
                                val newMessage = Message(idForum, message.profileImage, message.nickname, message.messageContent, message.forumId, message.postTime)
                                listOfMessage.add(newMessage)
                                messageAdapter.notifyDataSetChanged()
                            }
                        }
                        if (!noPullDown){
                            runOnUiThread {
                                messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                            }
                        }
                        else if(messageAdapter.itemCount > 0){
                            messageRecyclerView.smoothScrollToPosition(0)
                        }
                        noPullDown = false
                        setLastMessageId(servLastMessageId)
                    }
                    Thread.sleep(300)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        apiThread.start()
    }


    /**
     * Send a message to the server
     */
    private fun sendMessage(message : String,image: String, forum: Int){
        val apiThread = Thread {
            try {
                val nickname =intent?.extras?.getString("user").toString()
                DAO.postMessages( nickname,image,message, forum)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        apiThread.start()
    }
}