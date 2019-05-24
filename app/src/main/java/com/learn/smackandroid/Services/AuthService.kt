package com.learn.smackandroid.Services

import android.content.Context
import android.util.Log
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.learn.smackandroid.Utilities.URL_CREATE_USER
import com.learn.smackandroid.Utilities.URL_LOGIN
import com.learn.smackandroid.Utilities.URL_REGISTER
import org.json.JSONException

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var userToken = ""

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

        Volley.newRequestQueue(context).add(request)
    }

    fun login(context: Context, email: String, password: String, completion: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val request = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->

            println(response)

            try {
                userToken = response.getString("token")
                userEmail = response.getString("user")
                isLoggedIn = true
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

        Volley.newRequestQueue(context).add(request)
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
                header.put("Authorization", "Bearer $userToken")
                return header
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

}