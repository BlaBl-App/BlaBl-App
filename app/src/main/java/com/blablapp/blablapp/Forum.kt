package com.blablapp.blablapp

import com.google.gson.annotations.SerializedName

class Forum(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("description") var description: String,
) {
    override fun toString(): String {
        return "Forum(id=$id, name='$name', description='$description')"
    }
}