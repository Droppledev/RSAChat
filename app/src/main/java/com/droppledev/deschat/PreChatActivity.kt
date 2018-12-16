package com.droppledev.deschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_pre_chat.*

class PreChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_chat)

        val username = intent.getStringExtra("username")
        val keys = FirebaseDatabase.getInstance().reference.child("RSA")
        val rsa = RSA()
        lateinit var alicePubKey : List<Int>
        lateinit var bobPubKey : List<Int>
        var encKey : Int = -1
        var desKey : Int = -1

        var bobCounter = 0
        var aliceCounter = 0
        var keyCounter = 0

        if (username == "Alice"){
            rsa.init(13,11)
            alicePubKey = rsa.publicKey
            keys.push().setValue(hashMapOf("username" to "Alice","key" to alicePubKey))
            tv_progress.append("Alice Public Key Published ! : ${alicePubKey}\n")
            desKey = 3
            btn_chat.isEnabled = true
        }else if (username == "Bob"){
            rsa.init(7,13)
            bobPubKey = rsa.publicKey
            keys.push().setValue(hashMapOf("username" to "Bob","key" to bobPubKey))
            tv_progress.append("Bob Public Key Published ! : ${bobPubKey}\n")
        }

        btn_chat.setOnClickListener {
            if (desKey != -1){
                val intent = Intent(this,ChatActivity::class.java)
                intent.putExtra("key",desKey)
                intent.putExtra("username", username)
                startActivity(intent)
            }
        }

        keys.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                val key = dataSnapshot.value as Map<String, String>
                if (username == "Alice"){
                    val alicePrivKey = rsa.privateKey
                    if (key["username"] == "Bob" && bobCounter == 0){
                        bobPubKey = key["key"] as List<Int>
                        tv_progress.append("Got Bob Public Key : ${bobPubKey}\n")
                        Log.d("RSA",bobPubKey.toString())

                        // sign desKey with alicePrivKey
                        val signed = rsa.encrypt(desKey.toBigInteger(),alicePrivKey)
                        tv_progress.append("desKey Signed !\n")
                        // encrypt
                        val encrypt = rsa.encrypt(signed.toBigInteger(),bobPubKey)
                        tv_progress.append("desKey Encrypted !\n")
                        //push
                        keys.push().setValue(hashMapOf("username" to "Encrypted","key" to encrypt))
                        tv_progress.append("Encrypted desKey published !\n")
                        bobCounter++
                    }

                }
                else if (username == "Bob"){
                    val bobPrivKey = rsa.privateKey

                    if(key["username"] == "Alice" && aliceCounter == 0){
                        alicePubKey = key["key"] as List<Int>
                        tv_progress.append("Got Alice Public Key : ${alicePubKey}\n")
                        Log.d("RSA",alicePubKey.toString())
                        aliceCounter++
                    }
                    if (key["username"]=="Encrypted" && keyCounter==0){
                        val encKeyTemp = key["key"] as Long
                        tv_progress.append("Got Encrypted desKey \n")
                        encKey = encKeyTemp.toInt()
                        // decrypt
                        val decrypt = rsa.decrypt(encKey.toBigInteger(),bobPrivKey)
                        tv_progress.append("desKey decrypted \n")
                        //de-sign
                        desKey = rsa.decrypt(decrypt.toBigInteger(),alicePubKey)
                        tv_progress.append("desKey de-signed : ${desKey} \n\n")

                        btn_chat.isEnabled = true
                        keyCounter++
                    }

                }
                Log.d("RSA",key.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }
}
