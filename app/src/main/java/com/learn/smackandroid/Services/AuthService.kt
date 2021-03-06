package com.learn.smackandroid.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import org.json.JSONObject
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.learn.smackandroid.Controller.App
import com.learn.smackandroid.Utilities.*
import org.json.JSONException

object AuthService {

    fun registerUser(context: Context, username: String, email: String, password: String, completion: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("username", username)
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val request = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener {response ->
            println(response)
            completion(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Cannot do a request ${error.localizedMessage}")
            completion(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(request)
    }

    fun login(context: Context, email: String, password: String, completion: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val request = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->

            println(response)

            try {
                App.prefs.authToken = response.getString("token")
                App.prefs.userEmail = response.getString("user")
                App.prefs.isLoggedIn = true
                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                completion(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Cannot do a request: $error")
            completion(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(request)
    }

    fun createUser(context: Context, name: String, email:String, avatarName: String, avatarColor: String, completion: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarColor", avatarColor)
        jsonBody.put("avatarName", avatarName)
        val requestBody = jsonBody.toString()

        val request = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener {response ->

            println(response)

            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON", "Error decode JSON: ${e.localizedMessage}")
                completion(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Cannot add user: $error")
            completion(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header.put("Authorization", "Bearer ${App.prefs.authToken}")
                return header
            }
        }

        App.prefs.requestQueue.add(request)
    }


    fun findUserbyEmail(context: Context, completion: (Boolean) -> Unit) {
        val request = object : JsonObjectRequest(Method.GET, "$URL_GET_USER${App.prefs.userEmail}", null, Response.Listener { response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                broadCastUserDataChange(context)
                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON", "Error decode JSON: ${e.localizedMessage}")
                completion(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Cannot find user: $error")
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

    fun broadCastUserDataChange(context: Context) {
        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
        LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
    }

}