package com.learn.smackandroid.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.learn.smackandroid.Model.Channel
import com.learn.smackandroid.Utilities.URL_GET_CHANNELS
import com.learn.smackandroid.Utilities.URL_GET_USER
import org.json.JSONException

object MessageService {

    var channels = ArrayList<Channel>()

    fun getChannel(context: Context, completion: (Boolean) -> Unit) {
        val request = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            try {
                channels = ArrayList<Channel>()
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
                header.put("Authorization", "Bearer ${AuthService.userToken}")
                return header
            }
        }

        Volley.newRequestQueue(context).add(request)
    }
}