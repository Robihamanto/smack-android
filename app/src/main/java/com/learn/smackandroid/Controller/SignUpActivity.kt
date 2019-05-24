package com.learn.smackandroid.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.learn.smackandroid.R
import com.learn.smackandroid.Services.AuthService
import com.learn.smackandroid.Services.UserDataService
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    var userAvatar = "light0"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"


    fun avatarImageDidTap(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (color == 1) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resId = resources.getIdentifier(userAvatar, "drawable", packageName)
        avatarImageView.setImageResource(resId)
    }


    fun generateBackgroundButtonDidTap(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        avatarImageView.setBackgroundColor(Color.rgb(r, g, b))

        val savedColorR = r.toDouble() / 255
        val savedColorG = g.toDouble() / 255
        val savedColorB = b.toDouble() / 255

        avatarColor = "[$savedColorR, $savedColorG, $savedColorB, 1]"
    }

    fun signUpSignUpButtonDidTap(view: View) {
        val username = usernameTextField.text.toString()
        val email = emailTextField.text.toString()
        val password = passwordTextField.text.toString()

        AuthService.registerUser(this, username, email, password) { success ->
            if (success) {
                Log.d("Register", "Register Successful")
                AuthService.login(this, email, password) { success ->
                    if (success) {
                        Log.d("Login", "Login Successful")
                        AuthService.createUser(this, username, email, userAvatar, avatarColor) { success ->
                            if (success) {
                                println(UserDataService.name)
                                println(UserDataService.email)
                                println(UserDataService.avatarName)
                                println(UserDataService.avatarColor)
                                finish()
                            }
                        }
                    } else {
                        Log.d("Login", "Login Error, Something Happened")
                    }
                }
            } else {
                Log.d("Register", "Register Error, Something Happened")
            }
        }

    }


}
