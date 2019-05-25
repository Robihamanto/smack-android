package com.learn.smackandroid.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.learn.smackandroid.Model.Channel
import com.learn.smackandroid.R
import com.learn.smackandroid.Services.AuthService
import com.learn.smackandroid.Services.MessageService
import com.learn.smackandroid.Services.UserDataService
import com.learn.smackandroid.Utilities.BROADCAST_USER_DATA_CHANGE
import com.learn.smackandroid.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setupChannelAdapters()
        setupSocket()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

    private fun setupSocket() {
        socket.connect()
        socket.on("channelCreated", onNewChannel)
    }

    private fun setupChannelAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                usernameNavHeaderTextView.text = UserDataService.name
                emailNavHeaderTextView.text = UserDataService.email
                loginNavHeaderButton.text = "LOGOUT"
                val user = UserDataService
                val resId = resources.getIdentifier(user.avatarName, "drawable", packageName)
                userNavHeaderImageView.setBackgroundColor(UserDataService.getAvatarColor())
                userNavHeaderImageView.setImageResource(resId)
            } else {
                usernameNavHeaderTextView.text = ""
                emailNavHeaderTextView.text = ""
                loginNavHeaderButton.text = "LOGIN"
                userNavHeaderImageView.setBackgroundColor(Color.TRANSPARENT)
                userNavHeaderImageView.setImageResource(R.drawable.profiledefault)
            }
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginButtonDidTap(view: View) {

        val isLoggedIn = AuthService.isLoggedIn

        if (isLoggedIn) {
            UserDataService.logout()
            val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
            LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelButtonDidTap(view: View) {
        if (AuthService.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("Add") { dialog, which ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.channelNameTextField)
                    val descriptionTextField = dialogView.findViewById<EditText>(R.id.channelDescriptionTextField)

                    val channelName = nameTextField.text.toString()
                    val channelDescription = descriptionTextField.text.toString()

                    //create channel
                    socket.emit("newChannel", channelName, channelDescription)
                }
                .setNegativeButton("Cancel") {dialog, which ->
                }
                .show()
        }
    }

    fun fetchChannels() {
        if (!AuthService.isLoggedIn) return
        MessageService.getChannel(this) { success ->
            if (success) {
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDescriprion = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDescriprion, channelId)
            MessageService.channels.add(newChannel)
            channel_list.deferNotifyDataSetChanged()
        }
    }

    fun sendButtonDidTap(view: View) {

    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    fun broadCastUserDataChange() {
        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
        LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
    }
}
