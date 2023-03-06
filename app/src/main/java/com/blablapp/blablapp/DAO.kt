package com.blablapp.blablapp

import android.annotation.SuppressLint
import android.util.Log
import org.json.JSONObject
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class DAO {
    companion object {

        private var servIp: String = "http://tchoutchou.ovh:5555/api"


        fun getLastMessageId(forum: Int): Int {
            val url = URL(servIp+"/last_message_id")
            val postData = "forum=$forum".toByteArray(StandardCharsets.UTF_8)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("charset", "utf-8")
            conn.setRequestProperty("Content-Length", postData.size.toString())

            try {
                conn.outputStream.use { outputStream ->
                    outputStream.write(postData)
                }
            } catch (e: IOException) {
                // handle exception
            }
            val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            return JSONObject(jsonString).getInt("last_message_id")
        }

        fun getMessages(nb:Int = 10, start:Int = 0, forum: Int): Array<Message> {
            println("request $servIp/message?nb=$nb&start=$start&forum=$forum")
            val apiResponse = URL("$servIp/message?nb=$nb&start=$start&forum=$forum").readText()


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

        fun removeForum(forumId: Int) {
            val url = URL("$servIp/forums")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "DELETE"

            val postData = "id=$forumId".toByteArray(StandardCharsets.UTF_8)

            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Content-Length", postData.size.toString())

            try {
                connection.outputStream.use { outputStream ->
                    outputStream.write(postData)
                }
            } catch (e: IOException) {
                // handle exception

            }

            val responseCode = connection.responseCode
            // handle response code
            println("response code $responseCode")
        }

        fun addForum(forumName: String, forumDescription: String) {
            val url = URL("$servIp/forums")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true

            val postData = "name=$forumName&description=$forumDescription"
            connection.outputStream.write(postData.toByteArray(charset("UTF-8")))

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("Failed : HTTP error code : ${connection.responseCode}")
            }
        }
    }




}