package com.blablapp.blablapp

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/last_message_id")
    fun getLastMessageId(@Query("forum") forum: Int): Call<Int>

    @GET("/message")
    fun getMessages(@Query("nb") nb: Int = 10, @Query("start") start: Int = 0, @Query("forum") forum: Int): Call<Array<Message>>

    @POST("/message")
    fun postMessage(@Query("pic") profilePick: String, @Query("nickname") nickname: String, @Query("forum") forum: Int, @Query("message") messageContent: String): Call<Void>

    @GET("/forums")
    fun getAllForums(): Call<Array<Forum>>

    @DELETE("/forums")
    fun removeForum(@Query("id") forumId: Int): Call<Void>

    @POST("/forums")
    @FormUrlEncoded
    fun addForum(@Field("name") forumName: String, @Field("description") forumDescription: String): Call<Void>
}