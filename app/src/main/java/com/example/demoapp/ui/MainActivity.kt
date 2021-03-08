package com.example.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demoapp.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    val auth= FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()
        if(auth!=null){
            val intent= Intent(this, ChatActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent= Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}