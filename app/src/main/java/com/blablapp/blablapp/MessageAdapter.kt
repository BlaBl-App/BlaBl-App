package com.blablapp.blablapp

import android.content.Context
import android.graphics.BitmapFactory
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
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
            sentMessageViewHolder.userMessageSending.text = textWithPossibleLinks(message.messageContent)
            sentMessageViewHolder.userMessageSending.movementMethod = LinkMovementMethod.getInstance()
            sentMessageViewHolder.userDateSending.text = getDateFromTimestamp(message.postTime)

        } else {
            val receivedMessageViewHolder = holder as ReceivedMessageViewHolder

            receivedMessageViewHolder.userNameReceiving.text = message.nickname
            receivedMessageViewHolder.userMessageReceiving.text = textWithPossibleLinks(message.messageContent)
            receivedMessageViewHolder.userMessageReceiving.movementMethod = LinkMovementMethod.getInstance()
            receivedMessageViewHolder.userDateReceiving.text = getDateFromTimestamp(message.postTime)

            if (message.profileImage != ""){
                try {
                    val decodedBytes = Base64.decode(message.profileImage, Base64.NO_WRAP or Base64.URL_SAFE)
                    Log.d("IMAGE BYTE SIZE","decodedBytes.size= ${decodedBytes.size}")
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    //if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
                    if (bitmap != null) {
                        receivedMessageViewHolder.userPicReceiving.setImageBitmap(bitmap)
                    }
                }catch (e: IllegalArgumentException){
                    Log.e("ERROR LOADING IMAGE","$e")
                    Log.e("FAILED IMAGE","'\n${message.profileImage}\n'")
                }

            }


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

    private fun textWithPossibleLinks(message: String): SpannableString {
        val builder = SpannableString(message)
        val matcher = Patterns.WEB_URL.matcher(builder)
        while (matcher.find()) {
            val url = builder.subSequence(matcher.start(), matcher.end()).toString()
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(addHttp(url)))
                    startActivity(context,intent,null)
                }
            }
            builder.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return builder

    }

    private fun addHttp(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else {
            url
        }
    }

}