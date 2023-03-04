package com.blablapp.blablapp

import kotlin.time.Duration

class UserMessage(val idForum: Int, val postTime: Duration, val nickname: String , val profileImage: String ?, val message: String )
    : java.io.Serializable {}