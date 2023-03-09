package com.blablapp.blablapp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(private val context: Context, private val listOfMessage: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeSent = 1
    private val viewTypeReceived = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeSent) {
            val view = View.inflate(context, R.layout.message_custom_sent, null)
            SentMessageViewHolder(view)
        } else {
            val view = View.inflate(context, R.layout.message_custom_received, null)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return listOfMessage.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = listOfMessage[position]

        if (holder.javaClass == SentMessageViewHolder::class.java) {
            val sentMessageViewHolder = holder as SentMessageViewHolder
            sentMessageViewHolder.userNameSending.text = message.nickname
            sentMessageViewHolder.userMessageSending.text = message.messageContent
            sentMessageViewHolder.userDateSending.text = getDateFromTimestamp(message.postTime)

        } else {
            val receivedMessageViewHolder = holder as ReceivedMessageViewHolder
            receivedMessageViewHolder.userNameReceiving.text = message.nickname
            receivedMessageViewHolder.userMessageReceiving.text = message.messageContent
            receivedMessageViewHolder.userDateReceiving.text = getDateFromTimestamp(message.postTime)
            Log.e("RECEIVED IMAGE","'${message.profileImage}'")
            val decodedBytes = Base64.decode(message.profileImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            receivedMessageViewHolder.userPicReceiving.setImageBitmap(bitmap)

        }
    }

    override fun getItemViewType(position: Int): Int {
        val pseudo = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE).getString("pseudo", "")
        val message = listOfMessage[position]

        return if (message.nickname == pseudo) {
            viewTypeSent
        } else {
            viewTypeReceived
        }
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameSending: TextView = itemView.findViewById(R.id.userNameSending)
        val userMessageSending: TextView = itemView.findViewById(R.id.userMessageSending)
        val userDateSending: TextView = itemView.findViewById(R.id.userMessageTimeSending)
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameReceiving: TextView = itemView.findViewById(R.id.userNameReceiving)
        val userMessageReceiving: TextView = itemView.findViewById(R.id.userMessageReceiving)
        val userDateReceiving: TextView = itemView.findViewById(R.id.userMessageTimeReceiving)
        val userPicReceiving: ImageView = itemView.findViewById(R.id.userPic)
    }

    private fun getDateFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd LLL yyyy, HH:mm" , Locale.FRANCE)
        return format.format(date)
    }

}