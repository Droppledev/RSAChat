package com.droppledev.deschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_alice.setOnClickListener {
            val intent = Intent(this,PreChatActivity::class.java)
            intent.putExtra("username", "Alice")
            startActivity(intent)
        }
        btn_bob.setOnClickListener {
            val intent = Intent(this,PreChatActivity::class.java)
            intent.putExtra("username", "Bob")
            startActivity(intent)
        }
    }
}
