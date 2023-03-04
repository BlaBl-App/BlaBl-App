package com.blablapp.blablapp


import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

@SuppressLint("ViewConstructor")
class MessageCustom(context : Context, name : String, message: String, layout: ConstraintLayout) : View(context){

    init {
        val view = inflate(context, R.layout.message_custom_sent, null)
        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        view.findViewById<TextView>(R.id.userNameSending).text = name
        view.findViewById<TextView>(R.id.userMessageReceiving).text = message

        view.layoutParams = params
        layout.addView(view)
    }
}