package com.blablapp.blablapp

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(val context: Context, val listOfMessage: ArrayList<UserMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val VIEW_TYPE_SENT = 1
    val VIEW_TYPE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
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
            sentMessageViewHolder.userMessageSending.text = message.message
            sentMessageViewHolder.userDateSending.text = getDateFromTimestamp(message.postTime.toLong())

        } else {
            val receivedMessageViewHolder = holder as ReceivedMessageViewHolder
            receivedMessageViewHolder.userNameReceiving.text = message.nickname
            receivedMessageViewHolder.userMessageReceiving.text = message.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pseudo = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE).getString("pseudo", "")
        val message = listOfMessage[position]

        return if (message.nickname.toString() == pseudo) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userNameSending = itemView.findViewById<TextView>(R.id.userNameSending)
        val userMessageSending = itemView.findViewById<TextView>(R.id.userMessageSending)
        val userDateSending = itemView.findViewById<TextView>(R.id.userMessageTimeSending)
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userNameReceiving = itemView.findViewById<TextView>(R.id.userNameReceiving)
        val userMessageReceiving = itemView.findViewById<TextView>(R.id.userMessageReceiving)
        val userDateReceiving = itemView.findViewById<TextView>(R.id.userMessageTimeReceiving)
    }

    fun getDateFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return format.format(date)
    }

}