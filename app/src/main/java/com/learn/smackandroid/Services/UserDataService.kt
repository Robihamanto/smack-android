package com.learn.smackandroid.Services

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.LocalBroadcastManager
import com.learn.smackandroid.Controller.App
import com.learn.smackandroid.Utilities.BROADCAST_USER_DATA_CHANGE
import java.util.*

object UserDataService {

    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name  = ""

    fun getAvatarColor(components: String): Int {

        val strippedColor = components
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)

        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(r, g, b)
    }

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name  = ""

        App.prefs.authToken = ""
        App.prefs.userEmail = ""
        App.prefs.isLoggedIn = false
        MessageService.clearMessage()
        MessageService.clearChannel()
    }

}