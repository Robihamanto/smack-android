package com.learn.smackandroid.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.learn.smackandroid.R
import com.learn.smackandroid.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginButtonDidTap(view: View) {
        enableSpinner(true)

        val email = emailLoginTextField.text.toString()
        val password = passwordLoginTextField.text.toString()

        AuthService.login(this, email, password) { success ->
            if (success) {
                Log.d("LOGIN", "Login successfull")
                AuthService.findUserbyEmail(this) { success ->
                    if (success) {
                        Log.d("LOGIN", "User found")
                        enableSpinner(false)
                        finish()
                    } else {
                        errorToast()
                        Log.d("LOGIN", "User not found")
                    }
                }

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

    fun errorToast() {
        Toast.makeText(this, "Something wrong, Please try again.", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            loginSpinner.visibility = View.VISIBLE
        } else {
            loginSpinner.visibility = View.INVISIBLE
        }
        loginButton.isEnabled = !enable
        signUpLoginButton.isEnabled = !enable
    }

}
