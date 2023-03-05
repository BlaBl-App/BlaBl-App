package com.blablapp.blablapp

import android.annotation.SuppressLint
import android.util.Log
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class DAO {
    companion object {

        private var servIp: String = "http://tchoutchou.ovh:5555/api"


        fun getLastMessageId(): Int {
            val apiResponse = URL("$servIp/last_message_id").readText()
            val json = JSONObject(apiResponse)
            return json.getInt("last_message_id")
        }

        fun getMessages(nb:Int = 10, start:Int = 0): Array<Message> {
            println("request $servIp/message?nb=$nb&start=$start")
            val apiResponse = URL("$servIp/message?nb=$nb&start=$start").readText()


            return parseMessageJson(apiResponse)

        }

        fun postMessages(nickname:String, profilePick:String, messsageContent: String)
        {
            val url = URL(servIp+"/message")
            val postData="pick=$profilePick&nickname=$nickname&message=\"$messsageContent\""
            println("post data ${postData}")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true

            val wr = OutputStreamWriter(conn.outputStream)
            wr.write(postData)
            wr.flush()

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("Failed : HTTP error code : ${conn.responseCode}")
            }

            val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
            println(jsonString)
            conn.disconnect()
        }

        fun get_all_forums(): Array<Forum> {
            Log.d("DEGUB FORUM URL", "$servIp/forums")
            val apiResponse = URL("$servIp/forums").readText()
            val json = JSONObject(apiResponse)
            val forums = json.getJSONArray("forums")
            val forumsList = mutableListOf<Forum>()
            for (i in 0 until forums.length()) {
                val forum = forums.getJSONObject(i)
                val forumId = forum.getInt("id")
                val forumName = forum.getString("name")
                val forumDescription = forum.getString("description")
                forumsList.add(Forum(forumId, forumName, forumDescription))
            }
            return forumsList.toTypedArray()
        }
    }




}