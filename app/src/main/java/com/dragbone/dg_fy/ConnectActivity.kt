package com.dragbone.dg_fy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText

class ConnectActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        val pref = getPreferences(Context.MODE_PRIVATE)
        val serverAddress = pref.getString(getString(R.string.server_address), "192.168.0.1:4567")

        val input = findViewById(R.id.hostInput) as EditText
        input.setText(serverAddress)

        val button = findViewById(R.id.connectButton)
        button.setOnClickListener {
            val serverAddress = input.text.toString()
            MainActivity.host = serverAddress
            pref.edit().let {
                it.putString(getString(R.string.server_address), serverAddress)
                it.apply()
            }
            val myIntent = Intent(this@ConnectActivity, MainActivity::class.java)
            startActivity(myIntent)
        }
    }
}