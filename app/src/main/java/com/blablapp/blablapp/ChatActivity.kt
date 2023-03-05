package com.blablapp.blablapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.chat_activity.*

class ChatActivity : AppCompatActivity() {

    private var userName : String = ""
    private var linkImage : String = ""
    private var idForum : Int = -1
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var listOfMessage: ArrayList<UserMessage>
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
            getMessage()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            println("fake refresh")

            nbMessageToShow += 5
            noPullDown = true
            setLastMessageId(-1)
            swipeRefreshLayout.isRefreshing = false

        }

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
                val message = userTexForMsg.text.toString()
                sendMessage(message)
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
    fun getMaxMessageId(messages: Array<Message>): Int {
        return messages.maxByOrNull { it.id }?.id ?: Int.MIN_VALUE
    }

    fun getMinMessageId(messages: Array<Message>): Int {
        return messages.minByOrNull { it.id }?.id ?: Int.MAX_VALUE
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
                        println("old messageid ${getLastMessageId()} new messageID $servLastMessageId pull status $noPullDown")
                        val  messages : Array<Message> = DAO.Companion.getMessages(nbMessageToShow)
                        messages.reverse()
                        setLastMessageId(getMaxMessageId(messages))
                        runOnUiThread {
                            listOfMessage.clear()
                        }
                        for (message in messages) {
                            runOnUiThread {
                                listOfMessage.add(UserMessage(idForum, message.postTime, message.nickname, linkImage, message.messageContent))
                                //MessageCustom(this, message.nickname, message.messageContent, layout)
                                messageAdapter.notifyDataSetChanged()
                            }

                        }
                        if (!noPullDown){
                            println("pulling down")
                            runOnUiThread {
                                messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                            }
                        }else{
                            runOnUiThread {
                                messageRecyclerView.smoothScrollToPosition(12)
                            }
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

        // uncomment to activate api call test
        apiThread.start()

    }

    fun sendMessage(message : String){
        val apiThread = Thread {
            try {
                //DAO.Companion.getMessages()
                val nickname =intent?.extras?.getString("user").toString()

                DAO.Companion.postMessages( nickname,"",message)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // uncomment to activate api call test
        apiThread.start()
    }
}