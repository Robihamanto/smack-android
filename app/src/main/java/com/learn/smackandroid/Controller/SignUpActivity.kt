package com.learn.smackandroid.Controller

import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.learn.smackandroid.R
import com.learn.smackandroid.Services.AuthService
import com.learn.smackandroid.Services.UserDataService
import com.learn.smackandroid.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUpSpinner.visibility = View.INVISIBLE
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
        enableSpinner(true)
        val username = usernameTextField.text.toString()
        val email = emailTextField.text.toString()
        val password = passwordTextField.text.toString()

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(this, username, email, password) { success ->
                if (success) {
                    Log.d("Register", "Register Successful")
                    AuthService.login(this, email, password) { success ->
                        if (success) {
                            Log.d("Login", "Login Successful")
                            AuthService.createUser(this, username, email, userAvatar, avatarColor) { success ->
                                if (success) {
                                    broadCastUserDataChange()
                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            Log.d("Login", "Login Error, Something Happened")
                            errorToast()
                        }
                    }
                } else {
                    Log.d("Register", "Register Error, Something Happened")
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Please fill all field.", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    fun broadCastUserDataChange() {
        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
        LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            signUpSpinner.visibility = View.VISIBLE
        } else {
            signUpSpinner.visibility = View.INVISIBLE
        }
        signUpLoginButton.isEnabled = !enable
        generateBackgroundButton.isEnabled = !enable
        avatarImageView.isEnabled = !enable
    }


    fun errorToast() {
        Toast.makeText(this, "Something wrong, Please try again.", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

}
