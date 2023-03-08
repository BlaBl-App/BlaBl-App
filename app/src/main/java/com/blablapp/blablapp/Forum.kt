package com.blablapp.blablapp

class Forum(
    var id: Int,
    var name: String,
    var description: String,
) {
    override fun toString(): String {
        return "Forum(id=$id, name='$name', description='$description')"
    }
}