package com.learn.smackandroid.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.learn.smackandroid.Controller.App
import com.learn.smackandroid.Model.Channel
import com.learn.smackandroid.Model.Message
import com.learn.smackandroid.Utilities.URL_GET_CHANNELS
import com.learn.smackandroid.Utilities.URL_GET_MESSAGE
import com.learn.smackandroid.Utilities.URL_GET_USER
import org.json.JSONException

object MessageService {

    var channels = ArrayList<Channel>()
    var messages = ArrayList<Message>()

    fun fetchChannel(completion: (Boolean) -> Unit) {
        val request = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            try {
                //append channel to list of channels
                for (i in 0 until response.length()) {
                    val channel = response.getJSONObject(i)
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val id = channel.getString("_id")

                    val newChannel = Channel(name, desc, id)
                    channels.add(newChannel)
                }
                completion(true)
            } catch (e: JSONException) {
                Log.d("ERROR", "Error decode JSON: ${e.localizedMessage}")
                completion(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Cannot find channels: $error")
            completion(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header.put("Authorization", "Bearer ${App.prefs.authToken}")
                return header
            }
        }

        App.prefs.requestQueue.add(request)
    }

    fun fetchMessage(channelId: String, completion: (Boolean) -> Unit) {
        val request = object : JsonArrayRequest(Method.GET, "$URL_GET_MESSAGE$channelId", null, Response.Listener { response ->
            try {
                clearMessage()
                //append channel to list of channels
                for (i in 0 until response.length()) {
                    val message = response.getJSONObject(i)
                    val messageBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val username = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val timeStamp = message.getString("timeStamp")
                    val userAvatarColor = message.getString("userAvatarColor")

                    val newMessage = Message(messageBody, username, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    messages.add(newMessage)
                }
                completion(true)
            } catch (e: JSONException) {
                Log.d("ERROR", "Error decode JSON: ${e.localizedMessage}")
                completion(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Cannot find channels: $error")
            completion(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header.put("Authorization", "Bearer ${App.prefs.authToken}")
                return header
            }
        }

        App.prefs.requestQueue.add(request)
    }

    fun clearMessage() {
        messages.clear()
    }

    fun clearChannel() {
        messages.clear()
    }
}