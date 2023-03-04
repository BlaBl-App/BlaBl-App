package com.blablapp.blablapp

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class DAO {
    companion object {

        private var servIp: String = "http://tchoutchou.ovh:5555/api"




        fun getMessages(nb:Int = 10, start:Int = 0)
        {
            val apiResponse = URL(servIp+"/message").readText()
            val messages = parseMessageJson(apiResponse)
            for (message in messages) {
                println("id: ${message.id}, nickname: ${message.nickname}, profileImage: ${message.profileImage}")
            }
        }

        fun postMessages(nickname:String, profilePick:String, messsageContent: String)
        {
            val url = URL(servIp+"/message")
            val postData="pick=$profilePick&nickname=$nickname&message=$messsageContent"
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
    }




}