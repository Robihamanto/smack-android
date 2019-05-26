package com.learn.smackandroid.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.learn.smackandroid.Model.Message
import com.learn.smackandroid.R
import com.learn.smackandroid.Services.UserDataService

class MessageAdapter (val context: Context, val message: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  message.count()
    }

    override fun onBindViewHolder(parent: ViewHolder, position: Int) {
        parent.bindMessage(context, message[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView = itemView.findViewById<ImageView>(R.id.messageUserImageView)
        val usernameTextView = itemView.findViewById<TextView>(R.id.messageUsernameTextView)
        val timeStampTextView = itemView.findViewById<TextView>(R.id.messageTimestampTextView)
        val messageBodyTextView = itemView.findViewById<TextView>(R.id.messageBodyTextView)

        fun bindMessage(context: Context, message: Message) {
            val resId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImageView.setImageResource(resId)
            userImageView.setBackgroundColor(UserDataService.getAvatarColor(message.userAvatarColor))
            usernameTextView.text = message.userName
            timeStampTextView.text = message.timeStamp
            messageBodyTextView.text = message.message
        }
    }
}