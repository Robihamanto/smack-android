package com.learn.smackandroid.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.learn.smackandroid.R
import com.learn.smackandroid.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginButtonDidTap(view: View) {
        val email = emailLoginTextField.text.toString()
        val password = passwordLoginTextField.text.toString()

        AuthService.login(this, email, password) { success ->
            if (success) {
                Log.d("LOGIN", "Login successfull")
            } else {
                Log.d("LOGIN", "Login error")
            }
        }

    }

    fun signUpLoginButtonDidTap(view: View) {
        val signUpIntent = Intent(this, SignUpActivity::class.java)
        startActivity(signUpIntent)
        finish()
    }


}
