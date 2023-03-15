package com.blablapp.blablapp

import android.util.Log
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class DAO {

    companion object {
        private lateinit var servIp: String
        private lateinit var retrofit: Retrofit
        private lateinit var  apiService: ApiService

        fun getLastMessageId(forum: Int): Int {
            val response = apiService.getLastMessageId(forum).execute()
            return response.body() ?: 0
        }

        fun setServIp(ip: String, port: String = "8080", protocol: Int) {
            Log.d("DEBUG SET SERV IP ", "$protocol://$ip:$port")
            // 0 = http, 1 = https
            val serverProtocol: String = if (protocol == 0){
                "https://"
            } else{
                "http://"
            }
            //check if ip contains http or https, if not add it
            servIp = if (!ip.contains("http://") && !ip.contains("https://")) {
                concatenateIpAndPort(ip, port, serverProtocol)
            } else {
                concatenateIpAndPort(ip.split('/')[1].substring(3), port, serverProtocol)
            }
            Log.d("DEBUG SERV IP ", servIp)
            initApiService()
        }

        private fun initApiService(){
            retrofit = Retrofit.Builder()
                .baseUrl(servIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }

        private fun concatenateIpAndPort(ip: String, port: String, protocol: String): String {
            return "$protocol$ip:$port/api/"
        }

        fun getMessages(nb: Int = 10, start: Int = 0, forum: Int): Array<Message> {
            val response = apiService.getMessages(nb, start, forum).execute()
            return response.body() ?: emptyArray()
        }

        fun postMessages(nickname: String, profilePick: String, messageContent: String, forum: Int) {
            val response = apiService.postMessage(profilePick, nickname, forum, messageContent).execute()
            if (!response.isSuccessful) {
                throw RuntimeException("Failed : HTTP error code : ${response.code()}")
            }
        }

        fun getAllForums(): Array<Forum> {
            val response = apiService.getAllForums().execute()
            return response.body() ?: emptyArray()
        }

        fun removeForum(forumId: Int) {
            val response = apiService.removeForum(forumId).execute()
            if (!response.isSuccessful) {
                throw RuntimeException("Failed : HTTP error code : ${response.code()}")
            }
        }

        fun addForum(forumName: String, forumDescription: String) {
            val response = apiService.addForum(forumName, forumDescription).execute()
            if (!response.isSuccessful) {
                throw RuntimeException("Failed : HTTP error code : ${response.code()}")
            }
        }
    }
}
