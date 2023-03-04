package com.blablapp.blablapp
/*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Message(
    @SerializedName("id") var id: Int,
    @SerializedName("postTime") var postTime: Int,
    @SerializedName("nickname") var nickname: String,
    @SerializedName("pick") var profileImage: String
)

fun parseMessageJson(json: String): Array<Message> {
    val gson = Gson()
    val jsonResponse = gson.fromJson(json, ApiResponse::class.java)
    return jsonResponse.messages
}

class ApiResponse(
    @SerializedName("messages") var messages: Array<Message>,
    @SerializedName("success") var success: Boolean
)
*/