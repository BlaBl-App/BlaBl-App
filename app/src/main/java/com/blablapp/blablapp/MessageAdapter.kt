package com.blablapp.blablapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.method.LinkMovementMethod
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
import android.widget.Toast
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
            var textView: TextView = view.findViewById(R.id.userMessageSending)
            textView.movementMethod = LinkMovementMethod.getInstance()
            SentMessageViewHolder(view)
        } else {
            val view = View.inflate(context, R.layout.message_custom_received, null)
            var textView: TextView = view.findViewById(R.id.userMessageReceiving)
            textView.movementMethod = LinkMovementMethod.getInstance()
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
            sentMessageViewHolder.userMessageSending.setOnLongClickListener {
                //copy in clipboard
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("message", message.messageContent)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, "Message copied", Toast.LENGTH_SHORT).show()
                true
            }

        } else {
            val receivedMessageViewHolder = holder as ReceivedMessageViewHolder

            receivedMessageViewHolder.userNameReceiving.text = message.nickname
            receivedMessageViewHolder.userMessageReceiving.text = textWithPossibleLinks(message.messageContent)
            receivedMessageViewHolder.userMessageReceiving.movementMethod = LinkMovementMethod.getInstance()
            receivedMessageViewHolder.userDateReceiving.text = getDateFromTimestamp(message.postTime)
            receivedMessageViewHolder.userMessageReceiving.setOnLongClickListener {
                //copy in clipboard
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("message", message.messageContent)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, "Message copied", Toast.LENGTH_SHORT).show()
                true

            if (message.profileImage != ""){
                try {
                    val decodedBytes = Base64.decode(message.profileImage, Base64.NO_WRAP or Base64.URL_SAFE)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    if (bitmap != null) {
                        Log.d("ERROR IN BITMAP","setting image to default")
                        receivedMessageViewHolder.userPicReceiving.setImageBitmap(bitmap)
                    }
                }catch (e: IllegalArgumentException){
                    Log.d("ERROR LOADING IMAGE","$e")
                }
            }
        }
    }


    /**
     * Check if the message is sent or received
     */
    override fun getItemViewType(position: Int): Int {
        val pseudo = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE).getString("pseudo", "")
        val message = listOfMessage[position]

        return if (message.nickname == pseudo) {
            viewTypeSent
        } else {
            viewTypeReceived
        }
    }


    /**
     * ViewHolder for sent messages
     */
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameSending: TextView = itemView.findViewById(R.id.userNameSending)
        val userMessageSending: TextView = itemView.findViewById(R.id.userMessageSending)
        val userDateSending: TextView = itemView.findViewById(R.id.userMessageTimeSending)
    }


    /**
     * ViewHolder for received messages
     */
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameReceiving: TextView = itemView.findViewById(R.id.userNameReceiving)
        val userMessageReceiving: TextView = itemView.findViewById(R.id.userMessageReceiving)
        val userDateReceiving: TextView = itemView.findViewById(R.id.userMessageTimeReceiving)
        val userPicReceiving: ImageView = itemView.findViewById(R.id.userPic)
    }


    /**
     * Convert timestamp to date
     */
    private fun getDateFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd LLL yyyy, HH:mm" , Locale.FRANCE)
        return format.format(date)
    }


    /**
     * Convert text to clickable link if it's a link
     */
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


    /**
     * Add http:// if it's not already in the link
     */
    private fun addHttp(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else {
            url
        }
    }

}