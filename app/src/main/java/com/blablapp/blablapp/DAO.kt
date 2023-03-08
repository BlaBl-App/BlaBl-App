package com.blablapp.blablapp

import android.util.Log
import org.json.JSONObject
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class DAO {
    companion object {

        private lateinit var servIp: String

        fun getLastMessageId(forum: Int): Int {
            val apiResponse = URL("$servIp/last_message_id?forum=$forum").readText()
            val json = JSONObject(apiResponse)
            return json.getInt("last_message_id")
        }

        fun setServIp(ip: String, port: String = "8080", protocol: String) {
            //check if ip contains http or https, if not add it
            if (!ip.contains("http://") && !ip.contains("https://")) {
                servIp = concatenateIpAndPort(ip, port, protocol)
            } else {
                servIp = concatenateIpAndPort(ip.split('/')[1].substring(3), port, protocol)
            }
            Log.d("DEBUG SERV IP ", servIp)
        }

        private fun concatenateIpAndPort(ip: String, port: String, protocol: String): String {
            return "$protocol$ip:$port/api"
        }

        fun getMessages(nb:Int = 10, start:Int = 0, forum: Int): Array<Message> {
            val apiResponse = URL("$servIp/message?nb=$nb&start=$start&forum=$forum").readText()
            return parseMessageJson(apiResponse)
        }

        fun postMessages(nickname:String, profilePick:String, messsageContent: String, forum: Int)
        {
            val url = URL("$servIp/message")
            val postData="pick=$profilePick&nickname=$nickname&forum=$forum&message=\"$messsageContent\""
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
            Log.d("DEBUG INSERT MESSAGE", jsonString)
            conn.disconnect()
        }

        fun getAllForums(): Array<Forum>{
            try {
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
            catch(e: ConnectException)
            {
                return arrayOf(Forum(-1, "No internet", "No internet"))
            }
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
                e.printStackTrace()
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("Failed : HTTP error code : $responseCode")
            }
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