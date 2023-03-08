package com.blablapp.blablapp

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

class Message(
    @SerializedName("id") var id: Int,
    @SerializedName("pic") var profileImage: String,
    @SerializedName("nickname") var nickname: String,
    @SerializedName("messageContent") var messageContent: String,
    @SerializedName("forum") var forumId: Int,
    @SerializedName("postTime") var postTime: Long
): java.io.Serializable

fun parseMessageJson(json: String): Array<Message> {
    val gson = Gson()
    val jsonResponse = gson.fromJson(json, ApiResponse::class.java)
    val messagesList = mutableListOf<Message>()

    for (messageArr in jsonResponse.messages) {
        val message = Message(
            id = (messageArr[0] as Double).roundToInt(),
            profileImage = messageArr[1] as String,
            nickname = messageArr[2] as String,
            messageContent = messageArr[3] as String,
            forumId = (messageArr[4] as Double).roundToInt(),
            postTime = (messageArr[5] as Double).toLong()
        )
        messagesList.add(message)
    }
    return messagesList.toTypedArray()
}

class ApiResponse(
    @SerializedName("messages") var messages: Array<Array<Any>>,
    @SerializedName("success") var success: Boolean
)
